package deoplice.processor.codegen;

import deoplice.processor.types.POJOField;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.util.List;
import java.util.function.Function;

import static deoplice.processor.codegen.Stuff.titleCase;
import static java.lang.String.format;

// TODO: jfc. https://area-51.blog/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
//       Getting the class object supplied to the annotation is complex

public class WithStrategy implements Function<Element, Boolean> {
    /**
     * TODO: nothing excluded via EXCLUDED.
     *
     * // WITH = $Object.with$FieldName(value);
     * // BUILDER = $OBject.$builder().$prefix$FieldName($value).$build()
     */
    @Override
    public Boolean apply(Element element) {
        if (element.getKind().equals(ElementKind.CLASS)) {
            return contains(element.getAnnotationMirrors(), "@With");
        } else if (element.asType().getKind().equals(TypeKind.DECLARED)) {
            List<? extends AnnotationMirror> annotations = ((DeclaredType) element.asType()).asElement().getAnnotationMirrors();
            return contains(annotations, "@With");
        } else {
            return false;
        }
    }

    public String getter(POJOField field) {
        return format("%s::get%s", field.getDeclaringClass(), titleCase(field.getName()));
    }

    public String setter(POJOField field) {
        return format("%s::with%s", field.getDeclaringClass(), titleCase(field.getName()));
    }


    static boolean contains(List<? extends AnnotationMirror> annotations, String target) {
        for (AnnotationMirror annotation : annotations) {
            if (annotation.toString().equals(target)) {
                return true;
            }
        }
        return false;
    }
}
