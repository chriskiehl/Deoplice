package deoplice.processor.processor.models;

import deoplice.annotation.Updatable;
import lombok.Value;
import lombok.With;

/**
 * This is to test
 */
public class NestedUpdatable {

    @With
    @Value
    @Updatable
    public static class InnerAnnotatedClass {
        String foobie;
    }
}
