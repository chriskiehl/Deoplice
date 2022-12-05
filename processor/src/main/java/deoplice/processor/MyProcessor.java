package deoplice.processor;

import deoplice.processor.codegen.ClassGenerator;
import deoplice.processor.codegen.Parser;
import deoplice.processor.types.POJOField;
import deoplice.processor.types.Registry;
import io.vavr.collection.Array;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes("deoplice.processor.Lensed")
public class MyProcessor extends AbstractProcessor {

//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "ASDASDSADASD");
//        super.init(processingEnv);
//        throw new RuntimeException("PLEASE");
//    }

    Array<POJOField> fields;
    Registry registry;


    /**
     * TODO: Class vs method annotation
     * TODO: Exlclusions
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Hay.");

        Set<? extends Element> annotatedHandlers = roundEnv.getElementsAnnotatedWith(Lensed.class);

        for (Element e : annotatedHandlers) {
            int a =10;
            processingEnv.getElementUtils().getAllAnnotationMirrors(e);
            fields = Parser.parseFields(processingEnv.getElementUtils(), e);
            registry = Parser.parse(e);
            String result = ClassGenerator.generateClass(registry);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, result);
            try {
                processingEnv.getFiler().createSourceFile(result);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            for (Element ee : e.getEnclosedElements()) {
                int b = 1;
            }
        }
//
//        annotatedHandlers.forEach(x -> {
//            Arrays.stream(x.getClass().getDeclaredFields()).forEach(q -> {
//                System.out.println(q);
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, q.toString());
//            });
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, x.toString());
//        });



//        try {
//            JavaFileObject file = processingEnv.getFiler().createSourceFile("MyFirstClass");
//            Writer writer = file.openWriter();
//            writer.write("public class MyFirstClass {}");
//            writer.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

//        throw new RuntimeException("FIUDHID");
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> s = new HashSet<>();
        s.add("deoplice.processor.Lensed");
        return s;
    }
}
