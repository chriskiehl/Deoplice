package example;

import deoplice.processor.processor.Processor;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Testie {

    public static class FooBar {
        String foo;
        Integer bar;
    }

    String read(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("C:\\Users\\Chris\\Documents\\deoplice\\lib\\src\\test\\java\\deoplice\\FieldTypes.java"));
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
        list.add(new CharSequenceJavaFileObject("deoplice.codegen.FieldTypes", exampleClass));
        List<String> optionList = new ArrayList<String>();
        // set compiler's classpath to be same as the runtime's
        optionList.addAll(Arrays.asList(
                "-classpath",
                System.getProperty("java.class.path"),
                "-s",
                "C:\\Users\\Chris\\Documents\\deoplice\\lib\\build\\generated\\sources\\annotationProcessor\\java\\main"
                ));

        JavaCompiler.CompilationTask result = compiler.getTask(null, null, null, optionList, null, list);
        ArrayList<Processor> ps = new ArrayList<Processor>() {{
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
