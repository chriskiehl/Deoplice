package deoplice.processor.codegen;

import deoplice.processor.types.AST;
import io.vavr.collection.Array;

import javax.lang.model.element.Element;

@FunctionalInterface
public interface MethodGenerator {
    Array<AST.ApiMethodDef> generateMethods(Element element);
}
