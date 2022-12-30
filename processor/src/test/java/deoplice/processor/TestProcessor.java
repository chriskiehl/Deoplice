package deoplice.processor;

import deoplice.lenses.API;
import deoplice.lenses.Lens;
import deoplice.processor.codegen.GrabBag;
import deoplice.processor.processor.Processor;
import deoplice.processor.types.AST;
import io.vavr.collection.Array;
import io.vavr.collection.Iterator;
import lombok.Value;
import lombok.With;
import lombok.val;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class TestProcessor {


    Lens<Foo, Bar> $bar = API.makeLens(Foo::getBar, Foo::withBar);
    Lens<Bar, Baz> $baz = API.makeLens(Bar::getBaz, Bar::withBaz);
    Lens<Baz, String> $value = API.makeLens(Baz::getValue, Baz::withValue);

    @Test
    public void asdfasdfadsfadsfasdf() {
        Foo f = new Foo(new Bar(new Baz("Hello!")));
        Lens<Foo, String> lens = $bar.compose($baz).compose($value);
        System.out.println(
                API.update(lens, x -> x.toUpperCase())
                        .andThen(API.update(lens, x -> "poopie!" + x))
                        .apply(f));
    }


    @Test
    public void benchmark() {
        System.out.println(String.valueOf(67));
        System.out.println((int) 'z');
        System.out.println((int) 'A');
        System.out.println((int) 'Z');
        System.out.println(Integer.toHexString((char) 'Z'));
        System.out.println(Integer.parseInt("11111111", 2));
        System.out.println(Integer.parseInt("FF", 16));

        timeit(10000, this::v0);
        timeit(10000, this::v1);
        timeit(10000, this::v1_a);
        timeit(10000, this::v1_b);
        timeit(10000, this::v2);
        timeit(10000, this::v3);
        timeit(10000, this::v4);
        timeit(10000, this::v5);
        timeit(10000, this::v6);
        timeit(10000, this::v7);
    }

    public <A> void timeit(int times, Supplier<A> f) {
        Array<Long> result = Array.fill(times, () -> {
            long start = System.nanoTime();
            f.get();
            return System.nanoTime() - start;
        });
//        System.out.println("Min: " + result.min());
        System.out.println("Avg: " + result.average());
//        System.out.println("Max: " + result.max());
    }


    public Array<String> v0() {
        return Array.of("ABCDREFGHIJKLMNOPQRSTUVWXYZ".split("")).crossProduct().map(x -> x._1 + x._2).toArray();
    }

    public List<String> v1() {
        List<String> countryCodes = new ArrayList<>(26*26);
        String[] alphabet = "ABCDREFGHIJKLMNOPQRSTUVWXYZ".split("");
        for (String i : alphabet) {
            for (String j : alphabet) {
                countryCodes.add(i + j);
            }
        }
        return countryCodes;
    }

    public List<String> v1_a() {
        List<String> countryCodes = new ArrayList<>(26*26);
        char[] alphabet = new char[]{'A', 'B', 'C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q', 'R','S','T','U','V','W','X','y','Z'};
        for (char i : alphabet) {
            for (char j : alphabet) {
                countryCodes.add("" + i + j);
            }
        }
        return countryCodes;
    }

    String[] internedAlphabet = new String[]{
            new String("A").intern(),
            new String("B").intern(),
            new String("C").intern(),
            new String("D").intern(),
            new String("E").intern(),
            new String("F").intern(),
            new String("G").intern(),
            new String("H").intern(),
            new String("I").intern(),
            new String("J").intern(),
            new String("K").intern(),
            new String("L").intern(),
            new String("M").intern(),
            new String("N").intern(),
            new String("O").intern(),
            new String("P").intern(),
            new String("Q").intern(),
            new String("R").intern(),
            new String("S").intern(),
            new String("T").intern(),
            new String("U").intern(),
            new String("U").intern(),
            new String("U").intern(),
            new String("U").intern(),
            new String("U").intern(),
            new String("U").intern()
    };

    public List<String> v1_b() {
        List<String> countryCodes = new ArrayList<>(26*26);
        for (String i : internedAlphabet) {
            for (String j : internedAlphabet) {
                countryCodes.add(i + j);
            }
        }
        return countryCodes;
    }

    public List<String> v2() {
        List<String> countryCodes = new ArrayList<>(26*26);
        String alphabet = "ABCDREFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i< alphabet.length(); i++) {
            for (int j = 0; j< alphabet.length(); j++) {
                countryCodes.add("" + alphabet.charAt(i) + alphabet.charAt(j));
            }
        }
        return countryCodes;
    }

    public List<String> v3() {
        List<String> countryCodes = new ArrayList<>(26*26);
        for (int i = 'A'; i < 'Z'; i++) {
            for (int j = 'A'; j< 'Z'; j++) {
                countryCodes.add("" + (char) i + (char)j);
            }
        }
        return countryCodes;
    }

    public String[] v4() {
        String[] countryCodes = new String[26*26];
        int idx = 0;
        for (int i = 'A'; i < 'Z'; i++) {
            for (int j = 'A'; j< 'Z'; j++) {
                countryCodes[idx++] = ("" + (char) i + (char)j);
            }
        }
        return countryCodes;
    }

    public String[] v5() {
        String[] countryCodes = new String[26*26];
        int idx = 0;
        for (int i = 'A'; i < 'Z'; i++) {
            for (int j = 'A'; j< 'Z'; j++) {
                countryCodes[idx++] = String.valueOf(new char[]{(char)i, (char)j});
            }
        }
        return countryCodes;
    }

    public String[] v6() {
        String[] countryCodes = new String[26*26];
        char[] tmp = new char[2];
        int idx = 0;
        for (int i = 'A'; i < 'Z'; i++) {
            for (int j = 'A'; j < 'Z'; j++) {
                tmp[0] = (char) i;
                tmp[1] = (char) j;
                countryCodes[idx++] = String.valueOf(tmp);
            }
        }
        return countryCodes;
    }

    public String[] v7() {
        String[] cc = new String[26*26];
        char[][] countryCodes = new char[26*26][2];
        int idx = 0;
        for (int i = 'A'; i < 'Z'; i++) {
            for (int j = 'A'; j < 'Z'; j++) {
                countryCodes[idx][0] = (char) i;
                countryCodes[idx][0] = (char) j;
                idx++;
            }
        }

        for (int i = 0; i < countryCodes.length; i++) {
            cc[i] = String.valueOf(countryCodes[i]);
        }
        return cc;
    }




    @With
    @Value
    public static class Foo {
        Bar bar;
    }

    @With
    @Value
    public static class Bar {
        Baz baz;
    }

    @Value
    @With
    public static class Baz {
        String value;
    }

    @Test
    public void asfasdf() {
        System.out.println("HEY");
    }

    String read(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append((char) b);
        }
        return builder.toString();
    }



    @Test
    public void adsfsadfa() {
        val xs = "a.b.C.D".split("\\.");

        List<String> classes = new ArrayList<>(xs.length);
        List<String> working = new ArrayList<>(xs.length);
        int i = 0;
        while (i < xs.length) {
            if (Character.isUpperCase(xs[i].charAt(0))) {
                working.add(xs[i]);
                classes.add(Array.ofAll(working).mkString("."));
                working.clear();
            } else {
                working.add(xs[i]);
            }
            i++;
        }
        System.out.println(classes);
    }

    @Test
    public void adsfsadfa2() {
        System.out.println(GrabBag.pkg(AST.Qualified.of("foo.bar.Baz")));
        System.out.println(GrabBag.pkg(AST.Qualified.of("foo.bar.Baz.A")));
        System.out.println(GrabBag.pkg(AST.Qualified.of("foo.bar.baz.A.B")));
        System.out.println(GrabBag.pkg(AST.Qualified.of("Baz")));
    }

    /**
     */
    @Test
    public void asdadsfsd() throws IOException {
        System.out.println("Hai");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StringWriter out = new StringWriter();

        String myfoo = "C:\\Users\\Chris\\Documents\\deoplice\\processor\\src\\test\\java\\deoplice\\processor\\MyFoo.java";
        String another = "C:\\Users\\Chris\\Documents\\deoplice\\processor\\src\\test\\java\\deoplice\\processor\\AnotherClassFile.java";
        String nested = "C:\\Users\\Chris\\Documents\\deoplice\\processor\\src\\test\\java\\deoplice\\processor\\NestedUpdatable.java";
        String exampleClass = read(myfoo);

        ArrayList<CharSequenceJavaFileObject> list = new ArrayList<>();
        list.add(new CharSequenceJavaFileObject("deoplice.processor.MyFoo", read(myfoo)));
        list.add(new CharSequenceJavaFileObject("deoplice.processor.AnotherClassFile", read(another)));
        list.add(new CharSequenceJavaFileObject("deoplice.processor.NestedUpdatable", read(nested)));
        List<String> optionList = new ArrayList<String>();
        // set compiler's classpath to be same as the runtime's
        optionList.addAll(Arrays.asList(
                "-classpath",
                System.getProperty("java.class.path"),
                "-s",
                "build\\generated\\sources\\annotationProcessor\\java\\test"
        ));

        JavaCompiler.CompilationTask result = compiler.getTask(null, null, null, optionList, null, list);
        ArrayList<Processor> ps = new ArrayList<>() {{
            add(new Processor());
        }};
        result.setProcessors(ps);
        System.out.println(result.call());
        int a = 123;

//        Types.Registry registry = Parser.parseClass(PurchaseOrder.class);
//        System.out.println(Classgen.generateClass(registry));
    }

    // stolen from: https://github.com/jOOQ/jOOR/blob/7c27785e5c82bd60bb73a5eee569f3d0fb4df01b/jOOR-java-8/src/main/java/org/joor/Compile.java
    static final class CharSequenceJavaFileObject extends SimpleJavaFileObject {
        final CharSequence content;

        public CharSequenceJavaFileObject(String className, CharSequence content) {
            super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }
}
