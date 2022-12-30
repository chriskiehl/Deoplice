package deoplice.processor.codegen;

import deoplice.processor.types.AST;
import deoplice.processor.types.Extractions;
import io.vavr.collection.Array;
import lombok.Value;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.io.IOException;

/**
 * What it says on the tin: main controller for generating the class files.
 * This fella handles:
 *   - Parsing the raw java AST
 *   - Generating Lens and API source files
 *   - Writing those source files to disk
 */
@Value
public class Controller {
    Parser parser;
    Generator assembler;
    Filer filer;


    public Array<AST.ClassDef> generateSourceFiles(Element element) {
        Array<Extractions.ExtractedField> result = parser.parseFields(element);
        return assembler.lensClasses2(result).append(assembler.apiClass2(result));


    }


}
