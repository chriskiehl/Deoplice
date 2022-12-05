package deoplice.processor.types;

import lombok.Builder;
import lombok.Value;
import lombok.With;

public interface Lens_ {


    /**
     * A description of a chunk of code implementing the Lens<A, B> interface.
     */
    @With
    @Value
    @Builder
    class Lens implements Lens_ {
        String apiGroup;
        String variableName;
        QualifiedType selfType;
        QualifiedType argumentType;
        String getter;
        String setter;
    }

    /**
     * A description of the composition of two lenses.
     */
    @Value(staticConstructor = "of")
    class Composition implements Lens_ {
        Lens_ f;
        Lens_ g;
    }
}
