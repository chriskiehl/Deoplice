package deoplice.processor.codegen;

import deoplice.processor.types.API;
import deoplice.processor.types.Lens_;
import deoplice.processor.types.Registry;
import io.vavr.collection.Array;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static java.lang.String.format;


public class ClassGenerator {

    public static String generateClass(Registry registry) {
        return format("deoplice.processor\n\n" +
                        "import deoplice.lenses.Lens;\n" +
                        "import java.util.function.Function;\n" +
                        "import static deoplice.lenses.API.makeLens;\n\n" +
                        "public class GeneratedAPIs {\n" +
                        "%s\n\n" +
                        "%s\n\n}",
                registry.getApis().values().map(ClassGenerator::apiGroup).mkString("\n\n"),
                registry.getLenses().values().map(ClassGenerator::lensGroup).mkString("\n\n")
        );
    }

    public static String lensGroup(Array<Lens_.Lens> lenses) {
        return format("public static class %s {\n\t%s}",
                lenses.head().getApiGroup(),
                lenses.map(ClassGenerator::render).mkString("\n\t")
        );
    }

    public static String apiGroup(Array<API> apis) {
        return format("public static class %s {\n\t%s}",
                apis.head().getApiGroup(),
                apis.map(ClassGenerator::render).mkString("\n\t")
        );
    }

    public static String render(Lens_.Lens lens) {
        return format("public static Lens<%s, %s> %s = makeLens(%s, %s);",
                lens.getSelfType(),
                lens.getArgumentType(),
                lens.getVariableName(),
                lens.getGetter(),
                lens.getSetter()
        );
    }

    public static String render(API api) {
        return format(
                "public static Function<%s, %s> %s(%s value) {\n" +
                        "\t\tLens<%s, %s> lens = %s;\n" +
                        "\t\treturn (%s obj) -> lens.set(obj, value);\n" +
                        "\t}\n",
                api.getSelfType(),
                api.getSelfType(),
                api.getMethodName(),
                api.getArgumentType(),
                api.getSelfType(),
                api.getArgumentType(),
                compose(api.getLens()),
                api.getSelfType()
        );
    }

    public static String compose(Lens_ lens) {
        return Match(lens).of(
                Case($(instanceOf(Lens_.Lens.class)), x -> x.getApiGroup() + "." + x.getVariableName()),
                Case($(instanceOf(Lens_.Composition.class)), x -> compose(x.getF()) + ".compose(" + compose(x.getG()) + ")")
        );
    }
}
