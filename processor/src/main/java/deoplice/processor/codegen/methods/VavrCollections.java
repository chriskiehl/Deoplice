package deoplice.processor.codegen.methods;

import deoplice.processor.codegen.GrabBag;
import deoplice.processor.types.AST;
import deoplice.processor.codegen.MethodGenerator;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import javax.lang.model.element.Element;
import java.util.function.Function;

import static deoplice.processor.codegen.GrabBag.*;

/**
 * Method delegation strategies for vavr's flavor of data structures.
 */
public class VavrCollections implements MethodGenerator {


    @Override
    public Array<AST.ApiMethodDef> generateMethods(Element element) {
        return supported.getOrElse(nonParameterizedType(element), Array.empty())
                .map(f -> f.apply(element))
                .filter(Option::isDefined)
                .map(Option::get);
    }


    static Map<String, Array<Function<Element, Option<AST.ApiMethodDef>>>> supported = HashMap.of(
            "io.vavr.collection.HashSet", Array.of(
                    delegate("add", GrabBag::firstTypeParameter),
                    delegate("addAll", GrabBag::typeOf),
                    delegate("diff", GrabBag::typeOf),
                    delegate("drop", e -> "java.lang.Integer"),
                    delegate("dropWhile", e -> predicate(firstTypeParameter(e))),
                    delegate("map", e -> fn(firstTypeParameter(e))),
                    delegate("remove", GrabBag::firstTypeParameter),
                    delegate("reject", e -> predicate(firstTypeParameter(e))),
                    delegate("take", e -> "java.lang.Integer"),
                    delegate("takeWhile", e -> predicate(firstTypeParameter(e)))
            ),
            "io.vavr.collection.Array", Array.of(
                    delegate("append", GrabBag::firstTypeParameter),
                    delegate("appendAll", GrabBag::typeOf),
                    delegate("drop", e -> "java.lang.Integer"),
                    delegate("dropWhile", e -> predicate(firstTypeParameter(e))),
                    delegate("map", e -> fn(firstTypeParameter(e))),
                    delegate("prepend", GrabBag::firstTypeParameter),
                    delegate("prependAll", GrabBag::typeOf),
                    delegate("remove", GrabBag::firstTypeParameter),
                    delegate("reject", e -> predicate(firstTypeParameter(e))),
                    delegate("removeAt", e -> "java.lang.Integer"),
                    delegate("take", e -> "java.lang.Integer"),
                    delegate("takeWhile", e -> predicate(firstTypeParameter(e)))
            ),
            "io.vavr.collection.List", Array.of(
                    delegate("append", GrabBag::firstTypeParameter),
                    delegate("appendAll", GrabBag::typeOf),
                    delegate("drop", e -> "java.lang.Integer"),
                    delegate("dropWhile", e -> predicate(firstTypeParameter(e))),
                    delegate("map", e -> fn(firstTypeParameter(e))),
                    delegate("prepend", GrabBag::firstTypeParameter),
                    delegate("prependAll", GrabBag::typeOf),
                    delegate("remove", GrabBag::firstTypeParameter),
                    delegate("reject", e -> predicate(firstTypeParameter(e))),
                    delegate("removeAt", e -> "java.lang.Integer"),
                    delegate("take", e -> "java.lang.Integer"),
                    delegate("takeWhile", e -> predicate(firstTypeParameter(e)))
            )
    );



    static Function<Element, Option<AST.ApiMethodDef>> delegate(String name, Function<Element, String> paramType) {
        return (Element e) -> !GrabBag.hasNoTypeVarsOrWildcards(e)
                ? Option.none()
                : Option.some(AST.ApiMethodDef.builder()
                    .action(name)
                    .params(standardParam(paramType.apply(e)))
                    .expression(("(x) -> x.METHOD_NAME(param1)")
                            .replace("METHOD_NAME", name))
                    .build());
    }



}
