package deoplice.codegen;

import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.lang.reflect.Field;

/**
 * Houses all the relevant types used throughout the
 * code generation / lensification process.
 */
public class Types {

    @Value
    @Builder
    public static class Registry {
        Map<String, Array<DSL>> apis;
        Map<String, Array<LensDescriptor.Lens>> lenses;
    }

    /**
     * Bootleg sum type for modeling the grammar of a
     * Lens and Lens composition. This is the intermediate
     * in-memory representation before serializing into code
     */
    public interface LensDescriptor {
        /**
         * A description of a chunk of code implementing the Lens<A, B> interface.
         */
        @Value
        @Builder
        public static class Lens implements LensDescriptor {
            String apiGroup;
            String variableName;
            Signature signature;
            String getter;
            String setter;
        }

        /**
         * A description of the composition of two lenses.
         */
        @Value(staticConstructor = "of")
        public static class Composition implements LensDescriptor {
            LensDescriptor f;
            LensDescriptor g;
        }
    }

    @Value
    @Builder
    public static class DSL {
        String dslGroup;
        String methodName;
        LensDescriptor lens;
        String objectType;
        String argType; // TODO: generics
    }

    @Value(staticConstructor = "of")
    public static class Signature {
        String objectType;
        String argumentType;
    }

    /**
     * A reflected Field along with the (recursive) lineage of where
     * it came from. e.g.
     *
     * class Foo
     *    Bar bar;
     *
     * class Bar
     *    Baz baz
     *
     * {field: baz, lineage: [Foo]}
     */
    @With
    @Value
    public static class FieldLineage {
        List<Field> lineage;
        Field field;
    }

}
