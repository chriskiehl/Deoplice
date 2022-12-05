package deoplice.codegen;

import deoplice.codegen.Types.LensDescriptor;
import deoplice.lenses.Lens;
import io.vavr.collection.Array;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.Element;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Function;

import static deoplice.lenses.API.makeLens;
import static deoplice.lenses.API.update;

public class Parser {

    static HashSet<Class<?>> collectionTypes = HashSet.of(
            java.util.List.class,
            ArrayList.class,
            Array.class
    );

    static Types.Registry parseClass(Class<?> cls) {
        Array<Types.FieldLineage> result = parseFields(cls);
        Array<LensDescriptor.Lens> lenses = result.map(x -> buildLens(x.getField()));
        Array<Types.DSL> api = result.map(Parser::buildDSL);
        return Types.Registry.builder()
                .lenses(lenses.groupBy(LensDescriptor.Lens::getApiGroup))
                .apis(api.groupBy(Types.DSL::getDslGroup))
                .build();
    }

    @Value
    @Builder(toBuilder = true)
    static class Result {
        Map<String, Array<LensDescriptor.Lens>> lenses;
        Array<Types.DSL> setters;
    }

    /**
     * "Parses" the hierarchical POJO structure into a flattened list of fields.
     */
    static Array<Types.FieldLineage> parseFields(Class<?> cls) {
        return Array.of(cls.getDeclaredFields()).foldLeft(Array.<Types.FieldLineage>empty(), (fields, field) -> {
            if (!field.getType().getName().startsWith("java.") && !collectionTypes.contains(field.getType())) {
                Array<Types.FieldLineage> nested = parseFields(field.getType());
                return fields.append(new Types.FieldLineage(List.empty(), field))
                        .appendAll(Array.ofAll(nested).map(prependLineage(field)));
            } else {
                return fields.append(new Types.FieldLineage(List.empty(), field));
            }
        });
    }

    static LensDescriptor.Lens buildLens(Field field) {
        String variableName = "$" + field.getName();
        String containingClass = field.getDeclaringClass().getName().replace("$", ".");
        Types.Signature signature = Types.Signature.of(
                field.getDeclaringClass().getTypeName().replace("$", "."),
                field.getType().getName().replace("$", "."));
        String getter = containingClass + "::get" + titleCase(field.getName());
        String wither = containingClass + "::with" + titleCase(field.getName());
        return LensDescriptor.Lens.builder()
                .apiGroup(field.getDeclaringClass().getSimpleName() + "Lens")
                .variableName(variableName)
                .signature(signature)
                .getter(getter)
                .setter(wither)
                .build();
    }

    static Types.DSL buildDSL(Types.FieldLineage field) {
        String rootClass = field.getLineage().headOption()
                .map(x -> x.getDeclaringClass().getSimpleName())
                .getOrElse(field.getField().getDeclaringClass().getSimpleName());
        String variableName = "set" + field.getLineage().map(x -> titleCase(x.getName())).mkString()
                + titleCase(field.getField().getName());
        LensDescriptor lens = field.getLineage().isEmpty()
                ? buildLens(field.getField())
                : field.getLineage().map(x -> (LensDescriptor) buildLens(x))
                    .append(buildLens(field.getField()))
                    .reduce(LensDescriptor.Composition::of);
        String objectType = field.getLineage().headOption()
                .map(x -> x.getType().getName())
                .getOrElse(field.getField().getDeclaringClass().getName());
        String argumentType = field.getField().getType().getName();
        return Types.DSL.builder()
                .lens(lens)
                .methodName(variableName)
                .dslGroup(rootClass + "API")
                .objectType(objectType.replace("$", "."))
                .argType(argumentType.replace("$", "."))
                .build();
    }

    static String titleCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static Lens<Types.FieldLineage, List<Field>> $lineage
            = makeLens(Types.FieldLineage::getLineage, Types.FieldLineage::withLineage);

    public static Function<Types.FieldLineage, Types.FieldLineage> prependLineage(Field field) {
        return update($lineage, xs -> xs.prepend(field));
    }
}
