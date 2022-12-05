package deoplice.processor.types;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class API {
    String apiGroup;
    VariableName methodName;
    Lens_ lens;
    QualifiedType selfType;
    QualifiedType argumentType;
}
