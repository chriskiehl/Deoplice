package deoplice.processor.codegen.methods;

import deoplice.processor.codegen.GrabBag;
import deoplice.processor.types.AST;
import deoplice.processor.codegen.MethodCreator;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import javax.lang.model.element.Element;
import java.util.function.Function;

import static deoplice.processor.codegen.GrabBag.*;
import static java.lang.String.format;

/**
 * Method delegation strategies for the java.util.Collection flavor
 * of data structures. This generates "immutable" (i.e. Copy-on-Write) modification
 * APIs on top of the standard mutable java APIs.
 *
 * Note: a concrete collection type is needed for processing interface types like
 * List and Set. The implementation class cannot be determined at compile time, so an
 * arbitrary "best guess" is chosen (namely, ArrayList, and HashSet). While this
 * shouldn't (in theory) cause problems if you're programming to abstractions, it could
 * cause issues if the user tries to upcast and finds themselves with a runtime cast exception.
 *
 * TODO: call this out in the README.
 */
public class JavaCollections implements MethodCreator {

    @Override
    public Array<AST.ApiMethodDef> generateMethods(Element element) {
        return supported.getOrElse(rawTypeOf(element), Array.empty()).map(f -> f.apply(element));
    }


    static Map<String, Array<Function<Element, AST.ApiMethodDef>>> supported = HashMap.of(
            "java.util.LinkedList", Array.of(
                    delegate("add", GrabBag::genericTypeOf, GrabBag::typeOf),
                    delegate("addAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegate("remove", GrabBag::genericTypeOf, GrabBag::typeOf),
                    delegate("removeAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegate("sort", e -> format("java.util.Comparator<%s>", genericTypeOf(e)), GrabBag::typeOf)
            ),
            "java.util.ArrayList", Array.of(
                    delegate("add", GrabBag::genericTypeOf, GrabBag::typeOf),
                    delegate("addAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegate("remove", GrabBag::genericTypeOf, GrabBag::typeOf),
                    delegate("removeAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegate("sort", e -> format("java.util.Comparator<%s>", genericTypeOf(e)), GrabBag::typeOf)
            ),
            "java.util.List", Array.of(
                    delegate("add", GrabBag::genericTypeOf, e -> format("java.util.ArrayList<%s>", genericTypeOf(e))),
                    delegate("addAll", GrabBag::typeOf, e -> format("java.util.ArrayList<%s>", genericTypeOf(e))),
                    delegateStream("map", e -> fn(GrabBag.genericTypeOf(e)), e -> format("java.util.ArrayList<%s>", genericTypeOf(e))),
                    delegateStream("filter", e -> predicate(GrabBag.genericTypeOf(e)), e -> format("java.util.ArrayList<%s>", genericTypeOf(e))),
                    delegate("remove", GrabBag::genericTypeOf, e -> format("java.util.ArrayList<%s>", genericTypeOf(e))),
                    delegate("removeAll", GrabBag::typeOf, e -> format("java.util.ArrayList<%s>", genericTypeOf(e))),
                    delegate("sort", e -> format("java.util.Comparator<%s>", genericTypeOf(e)), e -> format("java.util.ArrayList<%s>", genericTypeOf(e)))
            ),
            "java.util.HashSet", Array.of(
                    delegate("add", GrabBag::genericTypeOf, GrabBag::typeOf),
                    delegate("addAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegate("remove", GrabBag::genericTypeOf, GrabBag::typeOf),
                    delegate("removeAll", GrabBag::typeOf, GrabBag::typeOf)
            ),
            "java.util.Set", Array.of(
                    delegate("add", GrabBag::genericTypeOf, e -> format("java.util.HashSet<%s>", genericTypeOf(e))),
                    delegate("addAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegate("remove", GrabBag::genericTypeOf, GrabBag::typeOf),
                    delegate("removeAll", GrabBag::typeOf, GrabBag::typeOf)
            )
    );


    public static Function<Element, AST.ApiMethodDef> delegate(String name, Function<Element, String> paramType, Function<Element, String> concreteDelegateType) {
        return (Element e) -> AST.ApiMethodDef.builder()
                .action(name)
                .params(standardParam(paramType.apply(e)))
                .expression(("(x) -> {\n" +
                        "DELEGATE_TYPE copy = x.stream().collect(java.util.stream.Collectors.toCollection(COL_TYPE::new));\n" +
                        "copy.METHOD_NAME(param1);\n" +
                        "return copy;\n" +
                        "}")
                        .replace("DELEGATE_TYPE", concreteDelegateType.apply(e))
                        .replace("METHOD_NAME", name)
                        .replace("COL_TYPE", concreteDelegateType.apply(e)))
                .build();
    }

    public static Function<Element, AST.ApiMethodDef> delegateStream(String name, Function<Element, String> paramType, Function<Element, String> concreteDelegateType) {
        return (Element e) -> AST.ApiMethodDef.builder()
                .action(name)
                .params(standardParam(paramType.apply(e)))
                .expression("(x) -> x.stream().METHOD_NAME(param1).collect(java.util.stream.Collectors.toCollection(COL_TYPE::new))"
                        .replace("METHOD_NAME", name)
                        .replace("COL_TYPE", concreteDelegateType.apply(e)))
                .build();
    }

}
