package deoplice.processor.codegen;


import io.vavr.collection.Array;
import io.vavr.collection.List;
import lombok.Value;
import lombok.With;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

public class Parser {

    @Value
    @With
    public static class FieldLineage {
        String name;
        String type;
        String declaringClass;
        // todo: arguments types.
        //       you've gotta recurse through all getArgumentTypes()
        List<String> lineage;
    }

    /*

    Tuple<A, Array<B>, C> af

    [a, [b], c]

    for type in args:
        if hasArguments type:
            type.qualified() + "<" + render(type.getTypeArguments()) + ">"
        else
            type.qualified()
    return ", ".join(...)


     */


    public static Function<FieldLineage, FieldLineage> prependLineage(String value) {
        return (field) -> field.withLineage(field.getLineage().prepend(value));
    }

    public static Array<FieldLineage> parseFields(Function<TypeMirror, Element> f, Element element) {
        return enclosedElements(element).foldLeft(Array.<FieldLineage>empty(), (fields, field) -> {
            if (!typeOf(field).startsWith("java.")) {
                Array<FieldLineage> nested = parseFields(f, f.apply(field.asType()));
                return fields.append(new FieldLineage(field.getSimpleName().toString(), typeOf(field), declaringClass(field), List.empty()))
                        .appendAll(Array.ofAll(nested).map(prependLineage(field.getSimpleName().toString())));
            } else {
                return fields.append(new FieldLineage(field.getSimpleName().toString(), declaringClass(field), typeOf(field), List.empty()));
            }
        });
    }

    static String declaringClass(Element field) {
        return ((TypeElement) ((DeclaredType) field.getEnclosingElement().asType()).asElement()).getQualifiedName().toString();
    }

    static Array<Element> enclosedElements(Element element) {
        return Array.ofAll(element.getEnclosedElements().stream()
                // for now, restricted to DECLARED types to make things easy
                .filter(x -> x.getKind() == ElementKind.FIELD && x.asType().getKind() == TypeKind.DECLARED));
    }

    static String typeOf(Element element) {
        return ((TypeElement) ((DeclaredType) element.asType()).asElement()).getQualifiedName().toString();
    }

}
