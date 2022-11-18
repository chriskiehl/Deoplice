import deoplice.processor.MyProcessor;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;

public class Testie {

    public static class FooBar {
        String foo;
        Integer bar;
    }

    @Test
    public void asdadsfsd() {
        System.out.println("Hai");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StringWriter out = new StringWriter();
        ArrayList<CharSequenceJavaFileObject> list = new ArrayList<>();
        list.add(new CharSequenceJavaFileObject(
                "org.joor.test.FailAnnotationProcessing",
                """
                         package org.joor.test;
                         import deoplice.processor.Lensed;
                         
                         import lombok.Value;
                         import lombok.With;
                         import java.time.LocalDateTime;
                         import io.vavr.Tuple3;
                         
                         @Value
                         @With
                         @Lensed
                         public class FailAnnotationProcessing {
                            String foo; 
                            Integer bar;
                            Foo inner;
                            Gloop<String> glipGlop; 
                            
                            public static class Foo {
                                String foobie; 
                                java.util.List<String> stingies;
                                Tuple3<Bob, java.util.List<LocalDateTime>, Integer> toop;
                            }
                            
                            public interface Gloop<A> {
                                A get(); 
                            }
                            
                            public static enum Bob {
                                ONE, 
                                TWO;
                            }
                         }
                        """
        ));
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
