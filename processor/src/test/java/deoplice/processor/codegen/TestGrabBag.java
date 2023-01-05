package deoplice.processor.codegen;

import deoplice.processor.types.AST;
import deoplice.processor.types.AST.Qualified;
import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.collection.Array;
import lombok.Value;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGrabBag {

    @Test
    public void titleCase() {
        val allLower = Array.range('a', 'z').map(firstLetter -> firstLetter + "word");
        val titleCased = Array.range('A', 'Z').map(firstLetter -> firstLetter + "word");
        allLower.zipWith(titleCased, (input, expected) -> {
            assertEquals(expected, GrabBag.titleCase(input));
            return Tuple.empty();
        });
    }

    @Test
    public void testUnqualify() {
        val testCases = Array.of(
            TestCase.of(Qualified.of("foo.bar.Baz"), "Baz"),
            TestCase.of(Qualified.of("foo.Bar"), "Bar"),
            TestCase.of(Qualified.of("foo.bar.Baz.Bazzer"), "Bazzer"),
            TestCase.of(Qualified.of("foo.bar.bar.Baz"), "Baz"),
            TestCase.of(Qualified.of("Baz"), "Baz")
        );
        testCases.forEach(testcase -> {
            assertEquals(testcase.getExpected(), GrabBag.unqualify(testcase.getInput()));
        });
    }

    @Test
    public void testPkg() {
        Array.of(
                TestCase.of(Qualified.of("Foo"), Array.of("Foo")),
                TestCase.of(Qualified.of("foo.Foo"), Array.of("foo.Foo")),
                TestCase.of(Qualified.of("foo.bar.Baz"), Array.of("foo.bar.Baz")),
                TestCase.of(Qualified.of("foo.bar.Baz.Haz"), Array.of("foo.bar.Baz", "Haz")),
                TestCase.of(Qualified.of("foo.Baz.Haz.Jazz"), Array.of("foo.bar.Baz", "Haz", "Jazz"))
        );

        val testcases = Array.of(
                TestCase.of(Qualified.of("Foo"), ""),
                TestCase.of(Qualified.of("foo.Foo"), "foo"),
                TestCase.of(Qualified.of("foo.bar.Baz"), "foo.bar"),
                TestCase.of(Qualified.of("foo.bar.Baz.Haz"), "foo.bar"),
                TestCase.of(Qualified.of("foo.Baz.Haz.Jazz"), "foo")
        );

        testcases.forEach(test -> {
            assertEquals(test.expected, GrabBag.pkg(test.input));
        });
    }

    @Test
    public void testsplitDeclaringClasses2() {
        val testCases = Array.of(
                TestCase.of(Qualified.of("Foo"), Array.of("Foo")),
                TestCase.of(Qualified.of("foo.Foo"), Array.of("foo.Foo")),
                TestCase.of(Qualified.of("foo.bar.Baz"), Array.of("foo.bar.Baz")),
                TestCase.of(Qualified.of("foo.bar.Baz.Haz"), Array.of("foo.bar.Baz", "Haz")),
                TestCase.of(Qualified.of("foo.Baz.Haz.Jazz"), Array.of("foo.Baz", "Haz", "Jazz"))
        );

        testCases.forEach(test -> {
            assertEquals(test.expected, GrabBag.splitDeclaringClasses2(test.input));
        });
    }

    @Value(staticConstructor = "of")
    static class TestCase<A, B> {
        A input;
        B expected;
    }
}
