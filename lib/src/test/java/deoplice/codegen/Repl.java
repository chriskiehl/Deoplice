package deoplice.codegen;


import com.google.common.collect.ImmutableList;
import deoplice.processor.Lensed;
import io.vavr.collection.Array;
import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.StringWriter;
import java.net.URI;
import java.time.LocalDateTime;

public class Repl {

    @With
    @Value
    @Lensed
    public static class PurchaseOrder {
        Integer age;
        LocalDateTime date;
        Approval approval;
    }

    @With
    @Value
    public static class Approval {
        String id;
        String status;
        Array<Level7Approver> approvalChain;
        LocalDateTime updatedOn;
        LocalDateTime createdOn;
        Confirmation confirmation;
    }

    @With
    @Value
    public static class Level7Approver {
        String alias;
        LocalDateTime approvedOn;
        String comments;
    }

    @With
    @Value
    public static class Confirmation {
        String confirmationNumber;
    }

    @Test
    public void asdadsfsd() {
        System.out.println("Hai");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StringWriter out = new StringWriter();
        JavaCompiler.CompilationTask result = compiler.getTask(null, null, null, null, null, ImmutableList.of(
                new CharSequenceJavaFileObject(
                        "org.joor.test.FailAnnotationProcessing",
                        """
                                 package org.joor.test; 
                                 @A
                                 public class FailAnnotationProcessing {
                                 }
                                """
                )
        ));

        System.out.println(result.call());

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
