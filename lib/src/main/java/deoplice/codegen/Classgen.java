package deoplice.codegen;

import io.vavr.collection.Array;

import static deoplice.codegen.Types.*;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static java.lang.String.format;

/**
 * Super-duper quick and dirty "renderer" for generating java
 * classes with our computed lens implementations and setter APIs.
 *
 * It currently generates ugly, ill-formatted java code, but... it saves lazy me finding a template library!
 */
public class Classgen {

    public static String generateClass(Registry registry) {
        return format("pacakge.todo\n\n" +
                "import deoplice.lenses.Lens;\n" +
                "import java.util.function.Function;\n" +
                "import static deoplice.lenses.API.makeLens;\n\n" +
                "public class GeneratedAPIs {\n" +
                "%s\n\n" +
                "%s\n\n}",
                registry.getApis().values().map(Classgen::apiGroup).mkString("\n\n"),
                registry.getLenses().values().map(Classgen::lensGroup).mkString("\n\n")
        );
    }

    public static String lensGroup(Array<LensDescriptor.Lens> lenses) {
        return format("public static class %s {\n\t%s}",
            lenses.head().getApiGroup(),
            lenses.map(Classgen::render).mkString("\n\t")
        );
    }

    public static String apiGroup(Array<DSL> apis) {
        return format("public static class %s {\n\t%s}",
                apis.head().getDslGroup(),
                apis.map(Classgen::render).mkString("\n\t")
        );
    }

    public static String render(LensDescriptor.Lens lens) {
        return format("public static Lens<%s, %s> %s = makeLens(%s, %s);",
            lens.getSignature().getObjectType(),
            lens.getSignature().getArgumentType(),
            lens.getVariableName(),
            lens.getGetter(),
            lens.getSetter()
        );
    }

    public static String render(DSL dsl) {
        return format(
            "public static Function<%s, %s> %s(%s value) {\n" +
                "\t\tLens<%s, %s> lens = %s;\n" +
                "\t\treturn (%s obj) -> lens.set(obj, value);\n" +
            "\t}\n",
                dsl.getObjectType(),
                dsl.getObjectType(),
                dsl.getMethodName(),
                dsl.getArgType(),
                dsl.getObjectType(),
                dsl.getArgType(),
                compose(dsl.getLens()),
                dsl.getObjectType()
        );
    }

    public static String compose(LensDescriptor lens) {
        return Match(lens).of(
            Case($(instanceOf(LensDescriptor.Lens.class)), x -> x.getApiGroup() + "." + x.getVariableName()),
            Case($(instanceOf(LensDescriptor.Composition.class)), x -> compose(x.getF()) + ".compose(" + compose(x.getG()) + ")")
        );
    }
}
