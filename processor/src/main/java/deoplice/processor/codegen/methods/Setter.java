package deoplice.processor.codegen.methods;

import deoplice.annotation.Updatable;
import deoplice.processor.types.AST;
import deoplice.processor.codegen.MethodGenerator;
import io.vavr.collection.Array;
import lombok.AllArgsConstructor;

import javax.lang.model.element.Element;

import static deoplice.processor.codegen.GrabBag.standardParam;
import static deoplice.processor.codegen.GrabBag.typeOf;

/**
 * Generates a standard Setter API method for (potentially deeply nested) fields.
 */
@AllArgsConstructor
public class Setter implements MethodGenerator {
    Updatable config;

    @Override
    public Array<AST.ApiMethodDef> generateMethods(Element element) {
        return Array.of(AST.ApiMethodDef.builder()
                .action("set")
                .params(standardParam(typeOf(element)))
                .expression("(x) -> param1")
                .build());
    }
}
