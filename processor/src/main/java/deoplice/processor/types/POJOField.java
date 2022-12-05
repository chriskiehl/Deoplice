package deoplice.processor.types;

import io.vavr.collection.List;
import lombok.Value;
import lombok.With;


@With
@Value
public class POJOField {
    VariableName name;
    // TODO: simple vs qualified
    QualifiedType type;
    QualifiedType declaringClass;
    List<POJOField> lineage;
}
