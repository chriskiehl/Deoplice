package deoplice.processor;

import deoplice.processor.codegen.Parser;
import io.vavr.collection.Array;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
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

    Array<Parser.FieldLineage> fields;


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Hay.");

        Set<? extends Element> annotatedHandlers = roundEnv.getElementsAnnotatedWith(Lensed.class);

        /*
        GETTING ROOT FIELDS
        -------------------

        ((TypeElement)annotatedHandlers.iterator().next()).getEnclosedElements().stream()
        .filter(x -> x.getKind() == ElementKind.FIELD)
        .toList()

        RECURSING INTO SUB-OBJECTS
        --------------------------
        processingEnv.getTypeUtils().asElement(((TypeElement) annotatedHandlers.iterator().next())
            .getEnclosedElements()
            .get(2)
            .asType()).getEnclosedElements()

       GETTING TYPE SIGNATURE (IS NOT EASY) (PARTIAL SOLUTION)
       -------------------------------------------------------

       ((TypeElement)((DeclaredType) ee.asType()).asElement()).getQualifiedName()
       What's happening here:
       Element(FIELD) -> Type -> Element(CLASS) -> ElementType -> getQualifiedName()

       When you're iterating the fields off the class, you find them as Elements of kind FIELD.
       Fields cannot be made into TypeElement directly, because this element is a FIELD. To get its
       type signature (at least for non-parameterized types), you've got to from FIELD to TYPE, but
       TypeMirrors can capture things like `NoType` or `NullType` which don't have class info, and so,
       after verifying that the Type is DECLARED (e.g. a Class or Interface), we can finally go back to
       Element, cast it to TypeElement, and get the qualified path!

       Holy Crackers!


         */

//        processingEnv.getTypeUtils().asElement()

        for (Element e : annotatedHandlers) {
            int a =10;
            fields = Parser.parseFields(processingEnv.getTypeUtils()::asElement, e);
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
