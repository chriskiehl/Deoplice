package deoplice.processor.codegen.extractor;

import deoplice.processor.processor.Extractor;
import io.vavr.NotImplementedError;
import io.vavr.Tuple;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import io.vavr.control.Validation;
import lombok.*;

import javax.lang.model.element.Element;

import static deoplice.processor.codegen.GrabBag.findAnnotation;

/**
 * TODO: Eventual plan is to support the toBuilder=true lombok variant
 */
public class BuilderStrategy implements Extractor {

    /**
     * TODO: Builder flavor.
     */
    public Boolean shouldExtract(Element element) {
        return builderAnnotation(element)
                .flatMap(this::toBuilderEnabled)
                .combine(this.getterAnnotationVariant(element))
                .ap((annotation, hasGetter) -> Tuple.empty())
                .isValid();
    }

    Validation<String, Builder> builderAnnotation(Element element) {
        val annotation = findAnnotation(element, Builder.class);
        return annotation.isDefined()
            ? Validation.valid(annotation.get())
            : Validation.invalid("No @Builder annotation present on this class. Skipping.");
    }

    Validation<String, Boolean> getterAnnotationVariant(Element element) {
        val variants = Array.of(Data.class, Value.class, Getter.class);
        val hasVariant = Option.traverse(variants, cls -> findAnnotation(element, cls)).isDefined();
        return hasVariant
                ? Validation.valid(hasVariant)
                : Validation.invalid("No `getter` providing annotation present. Needs one of @Value, @Data, or @Getter. Skipping.");
    }

    Validation<String, Builder> toBuilderEnabled(Builder annotation) {
        return annotation.toBuilder()
                ? Validation.invalid("toBuilder not enabled on the @Builder annotation. Skipping. ")
                : Validation.valid(annotation);
    }

    @Override
    public String getter(Element field) {
        throw new NotImplementedError("Hey, get outta here!");
    }

    @Override
    public String setter(Element field) {
        throw new NotImplementedError("Hey, get outta here!");
    }
}
