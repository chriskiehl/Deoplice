package deoplice.processor.processor;

import deoplice.annotation.Updatable;
import deoplice.processor.codegen.Generator;
import deoplice.processor.codegen.Controller;
import deoplice.processor.codegen.MethodGenerator;
import deoplice.processor.codegen.Parser;
import deoplice.processor.codegen.methods.JavaCollections;
import deoplice.processor.codegen.methods.Setter;
import deoplice.processor.codegen.methods.Updater;
import deoplice.processor.codegen.methods.VavrCollections;
import deoplice.processor.codegen.extractor.WithStrategy;
import io.vavr.collection.Array;
import lombok.val;

import javax.annotation.processing.Filer;

/**
 * We've gotta schlep around a fair bit of config at runtime based on what
 * the user has specified on the annotation. So, object creation is
 * all bundled up here so that all the newing can take place in one spot.
 */
public class FactoryFactoryFactoryFactoryFactory {

    /**
     * Make a controller with the appropriate configuration given the
     * user's supplied annotation preferences
     */
    public Controller controller(Updatable annotation, Filer filer) {
        val extractor = provideExtractor(annotation);
        val providers = provideMethodCreators(annotation);
        return new Controller(
                new Parser(annotation, extractor),
                new Generator(annotation, providers),
                filer
        );
    }

    Extractor provideExtractor(Updatable annotation) {
        // TODO: allow builder / bespoke strategies
        return new WithStrategy();
    }

    Array<MethodGenerator> provideMethodCreators(Updatable annotation) {
        // TODO: service loader? Although, I don't know why anyone would ever go through the pain
        return Array.of(
                new Setter(annotation),
                new Updater(annotation),
                new JavaCollections(),
                new VavrCollections()
        );
    }
}
