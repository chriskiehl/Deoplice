package deoplice.processor.codegen.extractor;

import deoplice.processor.codegen.GrabBag;
import deoplice.processor.processor.Extractor;
import io.vavr.Tuple;
import io.vavr.collection.Array;
import io.vavr.collection.HashSet;
import io.vavr.control.Option;
import io.vavr.control.Validation;
import lombok.*;

import javax.lang.model.element.Element;

import static deoplice.processor.codegen.GrabBag.findAnnotation;
import static java.lang.String.format;

/**
 * The withers and getters are grabbed on blind faith as strings. Because of Lombok's
 * black magic, there's no way to tell if the methods actually exist on the class at
 * annotation processing time. The assumption is that if the annotation is there, it's a
 * safe bet that the methods will eventually show up.
 */
public class WithStrategy implements Extractor {

    /**
     * Decides whether the fields owned by the element should be extracted
     * by the parser. For going after the @With annotation, that means all DeclaredTypes
     * must have both the target annotation (@With) as well as some kind of annotation
     * that provides an associated Getter.
     */
    public Boolean shouldExtract(Element element) {
        return findWitherAnnotation(element)
                .combine(findGetterAnnotationVariant(element))
                .ap((__, ___) -> Tuple.empty())
                .isValid();
    }

    Validation<String, With> findWitherAnnotation(Element element) {
        val annotation = findAnnotation(element, With.class);
        return annotation.isDefined()
                ? Validation.valid(annotation.get())
                : Validation.invalid("No @With annotation present on this class.");
    }

    Validation<String, Boolean> findGetterAnnotationVariant(Element element) {
        val variants = Array.of(Data.class, Value.class, Getter.class);
        val hasVariant = variants.map(x -> findAnnotation(element, x)).find(Option::isDefined).isDefined();
        return hasVariant
                ? Validation.valid(hasVariant)
                : Validation.invalid("No `getter` providing annotation present. Needs one of @Value, @Data, or @Getter.");
    }

    @Override
    public String getter(Element field) {
        // TODO: if using the @Getter annotation directly rather than one of its
        //       premixed flavors like @Data / @Value, `lombok.getter.noIsPrefix` needs to be
        //       honored. See: https://projectlombok.org/features/GetterSetter
        // We assume the default Lombok `is` boolean prefix for simplicity of initial release.
        val booleanTypes = HashSet.of("java.lang.Boolean", "boolean");
        String prefix = booleanTypes.contains(field.asType().toString()) ? "is" : "get";
        return String.format("%s::%s%s", GrabBag.declaringClass(field), prefix, GrabBag.titleCase(GrabBag.unqualifiedName(field)));
    }

    @Override
    public String setter(Element field) {
        return format("%s::with%s", GrabBag.declaringClass(field), GrabBag.titleCase(GrabBag.unqualifiedName(field)));
    }
}
