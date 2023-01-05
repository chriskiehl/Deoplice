package deoplice.processor.codegen;

import deoplice.processor.types.AST;
import deoplice.processor.types.Extractions;
import io.vavr.collection.Array;
import lombok.Value;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;

/**
 * What it says on the tin: main controller for generating the class files.
 * This fella handles:
 *   - Parsing the raw java AST
 *   - Generating Lens and API source files
 */
@Value
public class Controller {
    Parser parser;
    Generator assembler;
    Filer filer;

    public Array<AST.ClassDef> generateSourceClasses(Element element) {
        Array<Extractions.ExtractedField> result = parser.parseFields(element);
        return result.isEmpty()
                ? Array.empty()
                : assembler.lensClasses2(result).append(assembler.apiClass2(result));
    }

}
