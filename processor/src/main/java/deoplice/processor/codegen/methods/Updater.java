package deoplice.processor.codegen.methods;

import deoplice.annotation.Updatable;
import deoplice.processor.codegen.GrabBag;
import deoplice.processor.types.AST;
import deoplice.processor.codegen.MethodCreator;
import io.vavr.collection.Array;
import lombok.AllArgsConstructor;

import javax.lang.model.element.Element;

import static deoplice.processor.codegen.GrabBag.fn;

/**
 * Generates a functional Update API method for (potentially deeply nested) fields.
 */
@AllArgsConstructor
public class Updater implements MethodCreator {
    Updatable config;

    @Override
    public Array<AST.ApiMethodDef> generateMethods(Element element) {
        return Array.of(AST.ApiMethodDef.builder()
                .action("update")
                .params(Array.of(new AST.Parameter(AST.Qualified.of(fn(GrabBag.typeOf(element))), "param1")))
                .expression("(x) -> param1.apply(x)")
                .build());
    }
}
