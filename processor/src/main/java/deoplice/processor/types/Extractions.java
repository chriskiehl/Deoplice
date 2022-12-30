package deoplice.processor.types;

import io.vavr.collection.Array;
import io.vavr.collection.List;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.lang.model.element.Element;

public class Extractions {

    @With
    @Value
    @Builder
    public static class ExtractedField {
        Element element;
        String getter;
        String setter;
        List<ExtractedField> lineage;
    }
}
