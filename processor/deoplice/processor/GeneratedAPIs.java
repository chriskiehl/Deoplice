package deoplice.api;

import deoplice.lenses.Lens;

import java.util.function.Function;

import static deoplice.lenses.API.makeLens;

public class GeneratedAPIs {
    public static class ArbitraryUserClass2 {
        public static Function<deoplice.processor.FieldTypes.ArbitraryUserClass2, deoplice.processor.FieldTypes.ArbitraryUserClass2> setArray(io.vavr.collection.Array<java.lang.String> value) {
            Lens<deoplice.processor.FieldTypes.ArbitraryUserClass2, io.vavr.collection.Array<java.lang.String>> lens = deoplice.processor.FieldTypes.ArbitraryUserClass2Lens.$array;
            return (deoplice.processor.FieldTypes.ArbitraryUserClass2 obj) -> lens.set(obj, value);
        }

        public static Function<deoplice.processor.FieldTypes.ArbitraryUserClass2, deoplice.processor.FieldTypes.ArbitraryUserClass2> setDatetime(java.time.LocalDateTime value) {
            Lens<deoplice.processor.FieldTypes.ArbitraryUserClass2, java.time.LocalDateTime> lens = deoplice.processor.FieldTypes.ArbitraryUserClass2Lens.$datetime;
            return (deoplice.processor.FieldTypes.ArbitraryUserClass2 obj) -> lens.set(obj, value);
        }
    }

    public static class FieldTypes {
        public static Function<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes> setIntegers(java.lang.Integer value) {
            Lens<deoplice.processor.FieldTypes, java.lang.Integer> lens = deoplice.processor.FieldTypesLens.$integers;
            return (deoplice.processor.FieldTypes obj) -> lens.set(obj, value);
        }

        public static Function<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes> setSomeArbitraryDeclaredClass(deoplice.processor.FieldTypes.ArbitraryUserClass1 value) {
            Lens<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes.ArbitraryUserClass1> lens = deoplice.processor.FieldTypesLens.$someArbitraryDeclaredClass;
            return (deoplice.processor.FieldTypes obj) -> lens.set(obj, value);
        }

        public static Function<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes> setSomeArbitraryDeclaredClassBytes(byte value) {
            Lens<deoplice.processor.FieldTypes, byte> lens = deoplice.processor.FieldTypesLens.$someArbitraryDeclaredClass.compose(deoplice.processor.FieldTypes.ArbitraryUserClass1Lens.$bytes);
            return (deoplice.processor.FieldTypes obj) -> lens.set(obj, value);
        }

        public static Function<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes> setSomeArbitraryDeclaredClassString(java.lang.String value) {
            Lens<deoplice.processor.FieldTypes, java.lang.String> lens = deoplice.processor.FieldTypesLens.$someArbitraryDeclaredClass.compose(deoplice.processor.FieldTypes.ArbitraryUserClass1Lens.$string);
            return (deoplice.processor.FieldTypes obj) -> lens.set(obj, value);
        }

        public static Function<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes> setSomeArbitraryDeclaredClassAnotherLevelDown(deoplice.processor.FieldTypes.ArbitraryUserClass2 value) {
            Lens<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes.ArbitraryUserClass2> lens = deoplice.processor.FieldTypesLens.$someArbitraryDeclaredClass.compose(deoplice.processor.FieldTypes.ArbitraryUserClass1Lens.$anotherLevelDown);
            return (deoplice.processor.FieldTypes obj) -> lens.set(obj, value);
        }

        public static Function<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes> setSomeArbitraryDeclaredClassAnotherLevelDownArray(io.vavr.collection.Array<java.lang.String> value) {
            Lens<deoplice.processor.FieldTypes, io.vavr.collection.Array<java.lang.String>> lens = deoplice.processor.FieldTypesLens.$someArbitraryDeclaredClass.compose(deoplice.processor.FieldTypes.ArbitraryUserClass1Lens.$anotherLevelDown).compose(deoplice.processor.FieldTypes.ArbitraryUserClass2Lens.$array);
            return (deoplice.processor.FieldTypes obj) -> lens.set(obj, value);
        }

        public static Function<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes> setSomeArbitraryDeclaredClassAnotherLevelDownDatetime(java.time.LocalDateTime value) {
            Lens<deoplice.processor.FieldTypes, java.time.LocalDateTime> lens = deoplice.processor.FieldTypesLens.$someArbitraryDeclaredClass.compose(deoplice.processor.FieldTypes.ArbitraryUserClass1Lens.$anotherLevelDown).compose(deoplice.processor.FieldTypes.ArbitraryUserClass2Lens.$datetime);
            return (deoplice.processor.FieldTypes obj) -> lens.set(obj, value);
        }
    }

    public static class deoplice.processor.FieldTypesLens

    {
        public static Lens<deoplice.processor.FieldTypes, java.lang.Integer> $integers = makeLens(deoplice.processor.FieldTypes::getIntegers, deoplice.processor.FieldTypes::withIntegers);
        public static Lens<deoplice.processor.FieldTypes, deoplice.processor.FieldTypes.ArbitraryUserClass1> $someArbitraryDeclaredClass = makeLens(deoplice.processor.FieldTypes::getSomeArbitraryDeclaredClass, deoplice.processor.FieldTypes::withSomeArbitraryDeclaredClass);
    }

    public static class deoplice.processor.FieldTypes.ArbitraryUserClass1Lens

    {
        public static Lens<deoplice.processor.FieldTypes.ArbitraryUserClass1, byte> $bytes = makeLens(deoplice.processor.FieldTypes.ArbitraryUserClass1::getBytes, deoplice.processor.FieldTypes.ArbitraryUserClass1::withBytes);
        public static Lens<deoplice.processor.FieldTypes.ArbitraryUserClass1, java.lang.String> $string = makeLens(deoplice.processor.FieldTypes.ArbitraryUserClass1::getString, deoplice.processor.FieldTypes.ArbitraryUserClass1::withString);
        public static Lens<deoplice.processor.FieldTypes.ArbitraryUserClass1, deoplice.processor.FieldTypes.ArbitraryUserClass2> $anotherLevelDown = makeLens(deoplice.processor.FieldTypes.ArbitraryUserClass1::getAnotherLevelDown, deoplice.processor.FieldTypes.ArbitraryUserClass1::withAnotherLevelDown);
    }

    public static class deoplice.processor.FieldTypes.ArbitraryUserClass2Lens

    {
        public static Lens<deoplice.processor.FieldTypes.ArbitraryUserClass2, io.vavr.collection.Array<java.lang.String>> $array = makeLens(deoplice.processor.FieldTypes.ArbitraryUserClass2::getArray, deoplice.processor.FieldTypes.ArbitraryUserClass2::withArray);
        public static Lens<deoplice.processor.FieldTypes.ArbitraryUserClass2, java.time.LocalDateTime> $datetime = makeLens(deoplice.processor.FieldTypes.ArbitraryUserClass2::getDatetime, deoplice.processor.FieldTypes.ArbitraryUserClass2::withDatetime);
    }

}