package deoplice.processor.types;

import lombok.Value;

@Value(staticConstructor = "of")
public class VariableName {
    String value;

    @Override
    public String toString() {
        return this.value.toString();
    }
}
