package deoplice.processor.processor;

import deoplice.processor.types.AST;
import io.vavr.collection.Array;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static deoplice.processor.codegen.GrabBag.unqualify;

/**
 * Testing annotation processors, as it turns out, is a pretty massive pain
 * in the bum.
 *
 * Traditional unit testing is basically out for anything that
 * depends on the Element AST. To build anything, AFAICT, you'd need to JDK, or
 * to stub half the JDK like Lombok does, and all in order to then be able
 * to build AST trees by hand. In Java. One at a time. So... that's out.
 *
 * Instead, this goes super high level.
 *
 * Integration Tests (This file):
 * ------------------------------
 * We kick off annotation processing via the Java Compiler Tool. Assertions are
 * done against side effects.
 *
 * The Generated API (See: deoplice.examples.KitchenSinkTest)
 * ----------------------------------------------------------
 * The ultimate test is: "does it actually work?"
 * This file, by nature of being in a different package, runs through the real
 * annotation processing pipeline and outputs the generated files to the build
 * tool's specified location. It then just painstakingly calls every single generated
 * API method to make sure it behaves according to its contract.
 */
public class TestProcessor {

    static Path pkgdir = Paths.get("src","test","java","deoplice","processor", "processor", "models");
    static Path buildDir = Paths.get("build","generated","sources","annotationProcessor","java","test");


    /**
     * Processes a set of classes covering every possible TypeKind that can be
     * set on a Class as an ElementKind.FIELD.
     *
     * The user's package structure must be honored. So, classes are from various
     * spots in the package hierarchy to ensure that it gets mirrored in the generated files.
     *
     * Similarly, nested inner classes (and deeply nested inner classes) are used arbitrarily. As these
     * too much be handled correctly in order for all references to resolve.
     */
    @Test
    public void testFullAnnotationProcessing() throws IOException {
        clean(buildDir.toFile());

        Array<Path> classPaths = Array.of(
                pkgdir.resolve(Paths.get("KitchenSink.java")),
                pkgdir.resolve(Paths.get("nested", "pkg", "AnotherClassFile.java")),
                pkgdir.resolve(Paths.get("NestedUpdatable.java")),
                pkgdir.resolve(Paths.get("EmptyButAnnotationClass.java"))
        );

        Processor processor = new Processor();
        CompilationTask task = javaCompilationTask(processor, classPaths);
        task.call();
    }


    /**
     * Creates a ready to rock Java Compilation Task primed with our dependency class path
     * and default build directory.
     */
    CompilationTask javaCompilationTask(Processor processor, Array<Path> classes) {
        // Annotation processing is a compile-time process.
        // Which means we need to feed the raw source files into the compiler
        // in order *to be* compiled. This sounds very obvious, but assembling
        // this took a lot of trial and error...
        List<JavaSourceFile> classSources = classes.map(JavaSourceFile::new).toJavaList();

        List<String> compilerOptions = Arrays.asList(
                // Sets compiler's classpath to be same as the runtime's
                // This allows our dependencies (like Lombok) to be picked up.
                "-classpath",
                System.getProperty("java.class.path"),
                // and the output of the generated files to gradle's default location.
                // This is completely arbitrary. Gradle is unaware of these files since
                // they happen outside the *actual* annotation processing flow.
                "-s",
                buildDir.toString()
        );

        // Configuring the Java Compilation Task
        // In classic java fashion, the nulls are part of its API. Sane defaults are
        // applied for all null inputs.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        CompilationTask task = compiler.getTask(null, null, null, compilerOptions, null, classSources);
        task.setProcessors(Arrays.asList(processor));
        return task;
    }

    static class JavaSourceFile extends SimpleJavaFileObject {

        public JavaSourceFile(Path path) {
            super(path.toUri(), Kind.SOURCE);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return readSourceFile(Path.of(this.uri));
        }
    }


    /**
     * Reads a source file from our local directory.
     */
    static String readSourceFile(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append((char) b);
            }
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Even though we're "annotation processing", we're not *really* annotation
     * processing from Gradle's perspective, so it doesn't know to manage the
     * build dirs. We have to clean it up manually.
     */
    void clean(File root) throws IOException {
        for (File f : root.listFiles()) {
            if (f.isDirectory()) {
                clean(f);
            } else {
                Files.delete(Path.of(f.getAbsolutePath()));
            }
        }
    }
}
