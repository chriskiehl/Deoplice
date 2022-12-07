package deoplice.processor;

import deoplice.processor.codegen.ClassGenerator;
import deoplice.processor.codegen.Parser;
import deoplice.processor.types.POJOField;
import deoplice.processor.types.Registry;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

@SupportedAnnotationTypes("deoplice.processor.Lensed")
public class MyProcessor extends AbstractProcessor {

//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "ASDASDSADASD");
//        super.init(processingEnv);
//        throw new RuntimeException("PLEASE");
//    }

    Array<POJOField> fields;
    Registry registry = Registry.builder().lenses(HashMap.empty()).apis(HashMap.empty()).build();


    /**
     * TODO: Class vs method annotation
     * TODO: Exlclusions
     */
    @Override
    public boolean process(java.util.Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Hay.");

        HashSet<? extends Element> annotatedHandlers = HashSet.ofAll(roundEnv.getElementsAnnotatedWith(Lensed.class));
        // TODO: test that multiple @Lensed annotations
        Registry lensRegistry = annotatedHandlers.foldLeft(Registry.empty(), (registry, element) -> registry.merge(Parser.parse(element)));
        String classSource = ClassGenerator.generateClass(lensRegistry);
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile("deoplice.processor.GeneratedAPIs");
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                out.write(classSource);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

//        for (Element e : annotatedHandlers) {
//            int a = 10;
//            processingEnv.getElementUtils().getAllAnnotationMirrors(e);
//            fields = Parser.parseFields(processingEnv.getElementUtils(), e);
//            registry = registry.merge(Parser.parse(e));
//
//
////            String result = ClassGenerator.generateClass(registry);
////            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, result);
//
////            for (Element ee : e.getEnclosedElements()) {
////                int b = 1;
////            }
//        }

        return true;
    }

    @Override
    public java.util.Set<String> getSupportedAnnotationTypes() {
        java.util.HashSet<String> s = new java.util.HashSet<>();
        s.add("deoplice.processor.Lensed");
        return s;
    }
}
