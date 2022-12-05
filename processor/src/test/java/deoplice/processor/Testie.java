package deoplice.processor;

import deoplice.processor.MyProcessor;
import io.vavr.collection.Array;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Testie {

    public static class FooBar {
        String foo;
        Integer bar;
    }

    String read(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("./src/test/java/deoplice/processor/FieldTypes.java"));
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append((char) b);
        }
        return builder.toString();
    }


    /**
     */
    @Test
    public void asdadsfsd() throws IOException {
        System.out.println("Hai");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StringWriter out = new StringWriter();

        String exampleClass = read("");
        ArrayList<CharSequenceJavaFileObject> list = new ArrayList<>();
        list.add(new CharSequenceJavaFileObject("deoplice.processor.FieldTypes", exampleClass));
//        list.add(new CharSequenceJavaFileObject(
//                "deoplice.processor.FailAnnotationProcessing",
//    "                         package deoplice.processor;\n" +
//    "                         import deoplice.processor.Lensed;\n" +
//    "\n" +
//    "                         import lombok.Value;\n" +
////    "                         import lombok.With;\n" +
////    "                         import java.time.LocalDateTime;\n" +
////    "                         import io.vavr.Tuple3;\n" +
////    "\n" +
////    "                         @Value\n" +
////    "                         @With\n" +
//    "                         @Lensed\n" +
//    "                         public class FailAnnotationProcessing {\n" +
//    "                             \n" +
//    "                         }"
//                ));



//                         package org.joor.test;
//                         import deoplice.processor.Lensed;
//
//                         import lombok.Value;
//                         import lombok.With;
//                         import java.time.LocalDateTime;
//                         import io.vavr.Tuple3;
//
//                         @Value
//                         @With
//                         @Lensed
//                         public class FailAnnotationProcessing {
//                            String foo;
//                            Integer bar;
//                            Foo inner;
//                            Gloop<String> glipGlop;
//
//                            public static class Foo {
//                                String foobie;
//                                java.util.List<String> stingies;
//                                Tuple3<Bob, java.util.List<LocalDateTime>, Integer> toop;
//                            }
//
//                            public interface Gloop<A> {
//                                A get();
//                            }
//
//                            public static enum Bob {
//                                ONE,
//                                TWO;
//                            }
//                         }
//                        """
//        ));
        JavaCompiler.CompilationTask result = compiler.getTask(null, null, null, null, null, list);
        ArrayList<MyProcessor> ps = new ArrayList<MyProcessor>() {{
            add(new MyProcessor());
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
