package deoplice.processor.codegen;

import deoplice.processor.types.API;
import deoplice.processor.types.Config;
import deoplice.processor.types.Lens_;
import deoplice.processor.types.Lens_.Lens;
import deoplice.processor.types.POJOField;
import deoplice.processor.types.QualifiedType;
import deoplice.processor.types.VariableName;
import lombok.val;

import static deoplice.processor.codegen.Stuff.titleCase;
import static deoplice.processor.codegen.Stuff.unqualify;
import static java.lang.String.format;

public class LensBuilder {

    public static Lens identityLens(Lens lens) {
        return lens
                .withGetter("(x) -> x")
                .withSetter("(__, y) -> y")
                .withVariableName("$id" + lens.getVariableName());
    }

    public static Lens makeLens(Config conf, POJOField field) {
        val strat = new WithStrategy();
        return Lens.builder()
                .apiGroup(field.getDeclaringClass() + conf.getGroupPostfix())
                .variableName(conf.getLensPrefix() + field.getName())
                .argumentType(field.getType())
                .selfType(field.getDeclaringClass())
                .getter(strat.getter(field))
                .setter(strat.setter(field))
                .build();
    }

    public static API makeApi(Config conf, POJOField field) {
        QualifiedType rootClass = field.getLineage()
                .headOption().map(POJOField::getDeclaringClass)
                .getOrElse(field.getDeclaringClass());

        // TODO: clean this up with a prepended identity lens
        Lens_ lens = field.getLineage().isEmpty()
                ? makeLens(conf, field)
                : field.getLineage().map(x -> (Lens_) makeLens(conf, x))
                .append(makeLens(conf, field))
                .reduce(Lens_.Composition::of);

        return API.builder()
                .apiGroup(unqualify(rootClass))
                .argumentType(field.getType())
                .selfType(rootClass)
                .lens(lens)
                .methodName(nestedVariableName(conf, field))
                .build();
    }

    /**
     * API method names are build from concatenating each level in the hierachy
     * starting from the outermost.
     */
    static VariableName nestedVariableName(Config conf, POJOField field) {
        return VariableName.of(format("%s%s%s",
            conf.getApiPrefix(),
            field.getLineage().map(x -> titleCase(x.getName())).mkString(),
            titleCase(field.getName())
        ));
    }

}
