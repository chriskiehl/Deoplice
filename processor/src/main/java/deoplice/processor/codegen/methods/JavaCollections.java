package deoplice.processor.codegen.methods;

import deoplice.processor.codegen.GrabBag;
import deoplice.processor.codegen.MethodGenerator;
import deoplice.processor.types.AST;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

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
public class JavaCollections implements MethodGenerator {

    @Override
    public Array<AST.ApiMethodDef> generateMethods(Element element) {
        return supported.getOrElse(nonParameterizedType(element), Array.empty()).map(f -> f.apply(element))
                .filter(Option::isDefined)
                .map(Option::get);
    }

    static Map<String, Array<Function<Element, Option<AST.ApiMethodDef>>>> supported = HashMap.of(
            "java.util.LinkedList", Array.of(
                    delegate("add", GrabBag::firstTypeParameter, GrabBag::typeOf),
                    delegate("addAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegateStream("map", e -> fn(GrabBag.firstTypeParameter(e)), GrabBag::typeOf),
                    delegateStream("filter", e -> predicate(GrabBag.firstTypeParameter(e)), GrabBag::typeOf),
                    delegate("remove", GrabBag::firstTypeParameter, GrabBag::typeOf),
                    delegate("removeAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegate("sort", e -> format("java.util.Comparator<%s>", firstTypeParameter(e)), GrabBag::typeOf)
            ),
            "java.util.ArrayList", Array.of(
                    delegate("add", GrabBag::firstTypeParameter, GrabBag::typeOf),
                    delegate("addAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegateStream("map", e -> fn(GrabBag.firstTypeParameter(e)), JavaCollections::arraylist),
                    delegateStream("filter", e -> predicate(GrabBag.firstTypeParameter(e)), JavaCollections::arraylist),
                    delegate("remove", GrabBag::firstTypeParameter, GrabBag::typeOf),
                    delegate("removeAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegate("sort", e -> format("java.util.Comparator<%s>", firstTypeParameter(e)), GrabBag::typeOf)
            ),
            "java.util.List", Array.of(
                    delegate("add", GrabBag::firstTypeParameter, JavaCollections::arraylist),
                    delegate("addAll", GrabBag::typeOf, JavaCollections::arraylist),
                    delegateStream("map", e -> fn(GrabBag.firstTypeParameter(e)), JavaCollections::arraylist),
                    delegateStream("filter", e -> predicate(GrabBag.firstTypeParameter(e)), JavaCollections::arraylist),
                    delegate("remove", GrabBag::firstTypeParameter, JavaCollections::arraylist),
                    delegate("removeAll", GrabBag::typeOf, JavaCollections::arraylist),
                    delegate("sort", e -> format("java.util.Comparator<%s>", firstTypeParameter(e)), JavaCollections::arraylist)
            ),
            "java.util.HashSet", Array.of(
                    delegate("add", GrabBag::firstTypeParameter, GrabBag::typeOf),
                    delegate("addAll", GrabBag::typeOf, GrabBag::typeOf),
                    delegateStream("map", e -> fn(GrabBag.firstTypeParameter(e)), JavaCollections::hashset),
                    delegateStream("filter", e -> predicate(GrabBag.firstTypeParameter(e)), JavaCollections::hashset),
                    delegate("remove", GrabBag::firstTypeParameter, GrabBag::typeOf),
                    delegate("removeAll", GrabBag::typeOf, GrabBag::typeOf)
            ),
            "java.util.Set", Array.of(
                    delegate("add", GrabBag::firstTypeParameter,JavaCollections::hashset),
                    delegate("addAll", GrabBag::typeOf, JavaCollections::hashset),
                    delegateStream("map", e -> fn(GrabBag.firstTypeParameter(e)), JavaCollections::hashset),
                    delegateStream("filter", e -> predicate(GrabBag.firstTypeParameter(e)), JavaCollections::hashset),
                    delegate("remove", GrabBag::firstTypeParameter, JavaCollections::hashset),
                    delegate("removeAll", GrabBag::typeOf, JavaCollections::hashset)
            )
    );

    static String arraylist(Element e) {
        return format("java.util.ArrayList<%s>", firstTypeParameter(e));
    }

    static String hashset(Element e) {
        return format("java.util.HashSet<%s>", firstTypeParameter(e));
    }

    public static Function<Element, Option<AST.ApiMethodDef>> delegate(String name, Function<Element, String> paramType, Function<Element, String> concreteDelegateType) {
        return (Element e) -> {
            if (GrabBag.hasNoTypeVarsOrWildcards(e)) {
                return Option.some(AST.ApiMethodDef.builder()
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
                        .build());
            } else {
                return Option.none();
            }
        };
    }

    public static Function<Element, Option<AST.ApiMethodDef>> delegateStream(String name, Function<Element, String> paramType, Function<Element, String> concreteDelegateType) {
        return (Element e) -> {
            if (GrabBag.hasNoTypeVarsOrWildcards(e)) {
                return Option.some(AST.ApiMethodDef.builder()
                        .action(name)
                        .params(standardParam(paramType.apply(e)))
                        .expression("(x) -> x.stream().METHOD_NAME(param1).collect(java.util.stream.Collectors.toCollection(COL_TYPE::new))"
                                .replace("METHOD_NAME", name)
                                .replace("COL_TYPE", concreteDelegateType.apply(e)))
                        .build());
            } else {
                return Option.none();
            }
        };
    }

}
