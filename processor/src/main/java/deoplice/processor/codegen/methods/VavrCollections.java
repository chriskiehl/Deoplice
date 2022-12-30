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

/**
 * Method delegation strategies for vavr's flavor of data structures.
 */
public class VavrCollections implements MethodCreator {


    @Override
    public Array<AST.ApiMethodDef> generateMethods(Element element) {
        return supported.getOrElse(rawTypeOf(element), Array.empty()).map(f -> f.apply(element));
    }


    static Map<String, Array<Function<Element, AST.ApiMethodDef>>> supported = HashMap.of(
            "io.vavr.collection.HashSet", Array.of(),
            "io.vavr.collection.Set", Array.of(),
            "io.vavr.collection.Array", Array.of(
                    delegate("append", GrabBag::genericTypeOf),
                    delegate("appendAll", GrabBag::typeOf),
                    delegate("drop", e -> "java.lang.Integer"),
                    delegate("dropWhile", e -> predicate(genericTypeOf(e))),
                    delegate("map", e -> fn(genericTypeOf(e))),
                    delegate("prepend", GrabBag::genericTypeOf),
                    delegate("prependAll", GrabBag::typeOf),
                    delegate("remove", GrabBag::genericTypeOf),
                    delegate("reject", e -> predicate(genericTypeOf(e))),
                    delegate("removeAt", e -> "java.lang.Integer"),
                    delegate("take", e -> "java.lang.Integer"),
                    delegate("takeWhile", e -> predicate(genericTypeOf(e)))
            ),
            "io.vavr.collection.Seq", Array.of(),
            "io.vavr.collection.List", Array.of()
    );



    static Function<Element, AST.ApiMethodDef> delegate(String name, Function<Element, String> paramType) {
        return (Element e) -> AST.ApiMethodDef.builder()
                .action(name)
                .params(standardParam(paramType.apply(e)))
                .expression(("(x) -> x.METHOD_NAME(param1)")
                        .replace("METHOD_NAME", name))
                .build();
    }



}
