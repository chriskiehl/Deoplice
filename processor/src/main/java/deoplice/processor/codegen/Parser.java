package deoplice.processor.codegen;


import deoplice.annotation.Updatable;
import deoplice.processor.processor.Extractor;
import deoplice.processor.types.Extractions.ExtractedField;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import lombok.Value;

import javax.lang.model.element.Element;
import java.util.function.Function;

import static deoplice.processor.codegen.GrabBag.enclosedFields;

/**
 * Recursively traverses the Element AST to extract all
 * of its associated fields.
 */
@Value
public class Parser {
    Updatable config;
    Extractor extractor;

    public Array<ExtractedField> parseFields(Element element) {
        return parseFields(element, 0);
    }

    private Array<ExtractedField> parseFields(Element element, int recursionDepth) {
        if (extractor.shouldExtract(element) && recursionDepth < config.maxRecursionDepth()) {
            return GrabBag.enclosedFields(element).foldLeft(Array.empty(), (fields, field) -> {
                ExtractedField thisField = ExtractedField.builder()
                        .element(field)
                        .getter(extractor.getter(field))
                        .setter(extractor.setter(field))
                        .lineage(List.empty())
                        .build();
                Array<ExtractedField> nested = parseFields(field, recursionDepth + 1);
                return fields.append(thisField).appendAll(nested.map(prependLineage(thisField)));
            });
        } else {
            return Array.empty();
        }
    }

    public Function<ExtractedField, ExtractedField> prependLineage(ExtractedField value) {
        return (field) -> field.withLineage(field.getLineage().prepend(value));
    }
}
