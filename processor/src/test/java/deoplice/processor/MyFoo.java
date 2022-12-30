package deoplice.processor;

import deoplice.annotation.Updatable;
import deoplice.processor.NestedUpdatable.InnerAnnotatedClass;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.Array;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

@With
@Value
@Updatable
public class MyFoo<A> {
    // PrimitiveTypes
    byte bytes;
//    short shorts;
//    int ints;
//    long longs;
//    char chars;
//    float floats;
//    double doubles;
//    boolean booleans;
//
//    // ArrayTypes
    int[] primitiveArray1;
//    int[][] primitiveArray2;
//    int[][][] primitiveArray3;
//    Integer[] objectArray1;
//    Integer[][] objectArray2;
//    Integer[][][] objectArray3;
//
//    // Declared
//    String strings;
//    Integer integers;
    ArbitraryUserClass1 someArbitraryDeclaredClass;

//    ArbitraryUserClass2 anotherArbitraryClass;
//    ArbitraryUserInterface customerInterface;
//    ArbitraryUserEnum userEnum;
//
//    // Generics
//    // Some overlap with DeclaredTypes, but Wildcards are modeled with their
//    // own type in the JDK. So included here for completeness.
//    List<?> wildcard;
//    List<? extends Number> boundedWildcard;
//    List<String> concreteGeneric;
//
//    // TypeVar
//    A typeVar;
//
//    // complex chaos
//    Tuple3<A, String, Tuple2<List<A>, Integer>> complexDeclaredType;


    @With
    @Value
    public static class ArbitraryUserClass1 {
        byte bytes;
        String string;
        ArbitraryUserClass2 anotherLevelDown;
        NestedNestedInnerClass superNested;

        @Value
        @With
        public static class NestedNestedInnerClass {
            String whatever;
            AnotherClassFile anotherClassFile;
            InnerAnnotatedClass innerAnnotatedClass;
        }
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
