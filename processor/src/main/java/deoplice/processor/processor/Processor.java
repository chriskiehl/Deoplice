package deoplice.processor.processor;

import deoplice.annotation.Updatable;
import deoplice.processor.codegen.*;
import deoplice.processor.types.AST;
import io.vavr.collection.Array;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.Getter;
import lombok.Value;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.Writer;

@Getter
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("deoplice.annotation.Updatable")
public class Processor extends AbstractProcessor {
    private Set<AST.ClassDef> classDefs;

    @Override
    public boolean process(java.util.Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        HashSet<Element> elements = HashSet.ofAll(roundEnv.getElementsAnnotatedWith(Updatable.class));
        if (elements.isEmpty() && roundEnv.processingOver()) {
            return true;
        }
        try {
            Set<AST.ClassDef> classDefs = HashSet.ofAll(elements).flatMap(element -> {
                Updatable config = GrabBag.findAnnotation(element, Updatable.class).get();  // safe due to above check
                Controller controller = new FactoryFactoryFactoryFactoryFactory().controller(config, processingEnv.getFiler());
                return controller.generateSourceClasses(element);
            }).toSet();

            // These get stuffed onto the instance just to enable
            // checking the in memory representations during testing
            this.classDefs = classDefs;

            for (AST.ClassDef classDef : classDefs) {
                String pkg = classDef.getPkg();
                String clsname = classDef.getName();
                FileObject fileObj = processingEnv.getFiler().createSourceFile(pkg + "." + clsname);
                Writer writer = fileObj.openWriter();
                writer.append(Renderer.render(classDef));
                writer.close();
            }
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, Array.of(e.getStackTrace()).mkString("\n"));
            throw new RuntimeException(e);
        }
        return true;
    }
}
