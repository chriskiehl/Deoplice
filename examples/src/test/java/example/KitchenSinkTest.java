package example;

import deoplice.KitchenSink;
import deoplice.KitchenSink.ArbitraryUserClass1;
import deoplice.KitchenSink.ArbitraryUserClass2;
import deoplice.KitchenSink.ArbitraryUserEnum;
import deoplice.KitchenSink.ArbitraryUserInterface;
import io.vavr.collection.Array;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static deoplice.KitchenSinkAPI.*;


public class KitchenSinkTest {

    @Test
    public void testAllSetAndUpdateAPIMethodsBehave() {
        // we purposefully create a unpopulated classes here.
        // with the exception of its pimitive values, everything will
        // be null.
        KitchenSink ft = KitchenSink.builder().build();

        val arbitraryClass1 = new ArbitraryUserClass1((byte)1, "foo", new ArbitraryUserClass2(Array.empty(), LocalDateTime.now()));
        val arbitraryClass2 = new ArbitraryUserClass2(Array.empty(), LocalDateTime.now());
        val customInterface = new ArbitraryUserInterface() {};

        val primitiveArray1 = new int[]{1};
        val primitiveArray2 = new int[][]{{1}};
        val primitiveArray3 = new int[][][]{{{1}}};

        // Exercising all of the basic setters.
        val setters = setBytes(((byte)1))
                .andThen(setShorts((short) 2))
                .andThen(setInts(1))
                .andThen(setLongs(1L))
                .andThen(setChars('a'))
                .andThen(setFloats(1.0f))
                .andThen(setDoubles(1.0))
                .andThen(setBooleans(true))
                .andThen(setPrimitiveArray1(primitiveArray1))
                .andThen(setPrimitiveArray2(primitiveArray2))
                .andThen(setPrimitiveArray3(primitiveArray3))
                .andThen(setStrings("Hello!"))
                .andThen(setIntegers(1234))
                .andThen(setSomeArbitraryDeclaredClass(arbitraryClass1))
                .andThen(setAnotherArbitraryClass(arbitraryClass2))
                .andThen(setCustomerInterface(customInterface))
                .andThen(setUserEnum(ArbitraryUserEnum.ONE))
                .apply(ft);

        val updaters = updateBytes(__ -> ((byte)1))
                .andThen(updateShorts(__ -> (short) 2))
                .andThen(updateInts(__ -> 1))
                .andThen(updateLongs(__ -> 1L))
                .andThen(updateChars(__ -> 'a'))
                .andThen(updateFloats(__ -> 1.0f))
                .andThen(updateDoubles(__ -> 1.0))
                .andThen(updateBooleans(__ -> true))
                .andThen(updatePrimitiveArray1(__ -> primitiveArray1))
                .andThen(updatePrimitiveArray2(__ -> primitiveArray2))
                .andThen(updatePrimitiveArray3(__ -> primitiveArray3))
                .andThen(updateStrings(__ -> "Hello!"))
                .andThen(updateIntegers(__ -> 1234))
                .andThen(updateSomeArbitraryDeclaredClass(__ -> arbitraryClass1))
                .andThen(updateAnotherArbitraryClass(__ -> arbitraryClass2))
                .andThen(updateCustomerInterface(__ -> customInterface))
                .andThen(updateUserEnum(__ -> ArbitraryUserEnum.ONE))
                .apply(setters);

        // Set statements
        Assertions.assertEquals((byte)1, setters.getBytes());
        Assertions.assertEquals((short) 2, setters.getShorts());
        Assertions.assertEquals(1, setters.getInts());
        Assertions.assertEquals('a', setters.getChars());
        Assertions.assertEquals(1.0f, setters.getFloats());
        Assertions.assertEquals(1.0, setters.getDoubles());
        Assertions.assertEquals(true, setters.isBooleans());
        Assertions.assertEquals(primitiveArray1, setters.getPrimitiveArray1());
        Assertions.assertEquals(primitiveArray2, setters.getPrimitiveArray2());
        Assertions.assertEquals(primitiveArray3, setters.getPrimitiveArray3());
        Assertions.assertEquals("Hello!", setters.getStrings());
        Assertions.assertEquals(1234, setters.getIntegers());
        Assertions.assertEquals(arbitraryClass1, setters.getSomeArbitraryDeclaredClass());
        Assertions.assertEquals(arbitraryClass2, setters.getAnotherArbitraryClass());
        Assertions.assertEquals(customInterface, setters.getCustomerInterface());
        Assertions.assertEquals(ArbitraryUserEnum.ONE, setters.getUserEnum());

        // Update statements
        Assertions.assertEquals((byte)1, updaters.getBytes());
        Assertions.assertEquals((short) 2, updaters.getShorts());
        Assertions.assertEquals(1, updaters.getInts());
        Assertions.assertEquals('a', updaters.getChars());
        Assertions.assertEquals(1.0f, updaters.getFloats());
        Assertions.assertEquals(1.0, updaters.getDoubles());
        Assertions.assertEquals(true, updaters.isBooleans());
        Assertions.assertEquals(primitiveArray1, updaters.getPrimitiveArray1());
        Assertions.assertEquals(primitiveArray2, updaters.getPrimitiveArray2());
        Assertions.assertEquals(primitiveArray3, updaters.getPrimitiveArray3());
        Assertions.assertEquals("Hello!", updaters.getStrings());
        Assertions.assertEquals(1234, updaters.getIntegers());
        Assertions.assertEquals(arbitraryClass1, updaters.getSomeArbitraryDeclaredClass());
        Assertions.assertEquals(arbitraryClass2, updaters.getAnotherArbitraryClass());
        Assertions.assertEquals(customInterface, updaters.getCustomerInterface());
        Assertions.assertEquals(ArbitraryUserEnum.ONE, updaters.getUserEnum());
    }



}
