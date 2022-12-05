package deoplice.processor;

import deoplice.processor.codegen.WithStrategy;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.Array;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A POJO that declares fields of every possible type available to the
 * JDK as specified in `javax.lang.model.type`. Additionally,
 *
 * If deoplice can correctly process this object, there can not exist a possible
 * class variant that it cannot parse, as it would be outside of the JVM grammar.
 * In theory anyways.
 *
 * Relevant to Deoplice:
 *
 *  - ReferenceTypes
 *  - Primitives
 *  - DeclaredTypes
 *  - WildcardTypes
 *  - TypeVariables
 *
 * [0] All other interfaces in `javax.lang.model.type` are excluded as they're either
 * plumbing details of the parser itself, or capture parts of the language which don't apply to fields.
 */
@With
@Value
@Lensed(exclude = {"integers"})
public class FieldTypes<A> {
    // PrimitiveTypes
    byte bytes;
    short shorts;
    int ints;
    long longs;
    char chars;
    float floats;
    double doubles;
    boolean booleans;

    // ArrayTypes
    int[] primitiveArray1;
    int[][] primitiveArray2;
    int[][][] primitiveArray3;
    Integer[] objectArray1;
    Integer[][] objectArray2;
    Integer[][][] objectArray3;

    // Declared
    String strings;
    Integer integers;
    ArbitraryUserClass1 someArbitraryDeclaredClass;
    ArbitraryUserClass2 anotherArbitraryClass;
    ArbitraryUserInterface customerInterface;
    ArbitraryUserEnum userEnum;

    // Generics
    // Some overlap with DeclaredTypes, but Wildcards are modeled with their
    // own type in the JDK. So included here for completeness.
    List<?> wildcard;
    List<? extends Number> boundedWildcard;
    List<String> concreteGeneric;

    // TypeVar
    A typeVar;

    // complex chaos
    Tuple3<A, String, Tuple2<List<A>, Integer>> complexDeclaredType;

    public FieldTypes<A> withThingie(Integer x) {
        return null;
    }

    @With
    @Value
    public static class ArbitraryUserClass1 {
        byte bytes;
        String string;
        ArbitraryUserClass2 anotherLevelDown;
    }

    @With
    @Value
    public static class ArbitraryUserClass2 {
        Array<String> array;
        LocalDateTime datetime;
    }

    public interface ArbitraryUserInterface {
    }

    public enum ArbitraryUserEnum {ONE, TWO;}
}
