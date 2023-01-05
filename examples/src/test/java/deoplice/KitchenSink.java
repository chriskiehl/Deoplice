package deoplice;

import deoplice.annotation.Updatable;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.Array;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A POJO that declares fields of every possible user type[0] available to the
 * JDK as specified in `javax.lang.model.type`.
 *
 * If deoplice can correctly process this object, then (in theory) there cannot exist a possible
 * class variant that it cannot process, as it would be outside of the JVM grammar.
 *
 *
 * [0] All other interfaces in `javax.lang.model.type` are excluded as they're either
 * plumbing details of the parser itself, or capture parts of the language which don't apply
 * to things which can be ElementKind.FIELD.
 *
 */
@With
@Value
@Builder
@Updatable
public class KitchenSink {
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

    // Generics & Collection Types
    // We have one of each flavor here to exercise the
    // java and vavr collection method generation paths
    java.util.List<?> wildcard;
    java.util.ArrayList<? extends Number> boundedWildcard;
    java.util.List<String> concreteGeneric;

    // TypeVar

    // complex chaos
    Tuple3<String, String, Tuple2<List<?>, Integer>> complexDeclaredType;

    @With
    @Value
    public static class ArbitraryUserClass1 {
        byte bytes;
        String string;
        ArbitraryUserClass2 anotherLevelDown;
    }

    @With
    @Value
//    @Lensed
    public static class ArbitraryUserClass2 {
        Array<String> array;
        LocalDateTime datetime;
    }

    public interface ArbitraryUserInterface {
    }

    public enum ArbitraryUserEnum {ONE, TWO;}
}
