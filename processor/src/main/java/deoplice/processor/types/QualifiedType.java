package deoplice.processor.types;

import lombok.Value;

/**
 * A fully qualified name. e.g. com.foo.bar.MyThing
 */
@Value(staticConstructor = "of")
public class QualifiedType {
    String value;

    @Override
    public String toString() {
        return value.toString();
    }
}
