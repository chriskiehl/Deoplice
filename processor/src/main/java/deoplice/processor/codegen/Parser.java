package deoplice.processor.codegen;


import deoplice.processor.types.API;
import deoplice.processor.types.Config;
import deoplice.processor.types.Lens_;
import deoplice.processor.types.POJOField;
import deoplice.processor.types.QualifiedType;
import deoplice.processor.types.Registry;
import deoplice.processor.types.VariableName;
import io.vavr.collection.Array;
import io.vavr.collection.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import java.util.function.Function;

/**
 * Is toString() safe to use across different JDKs? I'm going with yes.
 * Corretto, OpenJDK, and Oracle all have "close enough" implementations
 * to not worry about for v1.0. If it proves to be an issue, we can always
 * roll our own concrete fully qualified type resolution by parsing the tree.
 */
public class Parser {

    public static Registry parse(Element element) {
        Array<POJOField> fields = parseFields(null, element);
        Array<Lens_.Lens> rawLenses = fields.map(x -> LensBuilder.makeLens(new Config(), x));
        Array<API> highlevelApi = fields.map(x -> LensBuilder.makeApi(new Config(), x));
        return Registry.builder()
            .lenses(rawLenses.groupBy(Lens_.Lens::getApiGroup))
            .apis(highlevelApi.groupBy(API::getApiGroup))
            .build();
    }

    public static Function<POJOField, POJOField> prependLineage(POJOField value) {
        return (field) -> field.withLineage(field.getLineage().prepend(value));
    }

    // TODO: support everything in package javax.lang.model.type.
    //       this is implicitly handled via toString(). So, really, this means that
    //       everything in .type needs to become a testcase.
    // TODO: recursion depth
    // TODO: Enable toggling the parent/child exclusion check
    // TODO: enclosed elements has a bunch of ad hoc experimental logic.
    //       this should be pulled out into a general "should process?" notion.
    // TODO: Standard exclusions for VAVR (for instance, serialVersionUID, EMPTY, etc...)
    // TODO: We can sort've get the Annotation info, but not the *result* of the annotations (due to
    //       processing order (over which we have no control)). Doing this can let us fail earlier and
    //       with better error messages.
    public static Array<POJOField> parseFields(Elements utils, Element element) {
        return parseFields(utils, element, 0);
    }

    private static Array<POJOField> parseFields(Elements utils, Element element, int recursionDepth) {
        // TODO: alright, the annotation check should solve all these problems..
        //       has prebaked annotation strategy ~~OR has user supplied strategy~~
        // Lens + Composition
        // (Field, [lineage]) -> (Lens, Composition{APIGroup)
        /*
        Graph -> List[Nodes] -> (Lens, Api)
         */
        if (!new WithStrategy().apply(element)) {
            return Array.empty();
        } else {
            return enclosedElements(element).foldLeft(Array.empty(), (fields, field) -> {
                POJOField thisField = toField(field);
                Array<POJOField> nested = parseFields(utils, field, recursionDepth + 1);
                return fields.append(thisField).appendAll(nested.map(prependLineage(thisField)));
            });
        }
    }


    static POJOField toField(Element field) {
        return new POJOField(VariableName.of(field.getSimpleName().toString()),
                QualifiedType.of(typeOf(field)),
                QualifiedType.of(declaringClass(field)),
                List.empty()
            );
    }

    static String declaringClass(Element field) {
        return ((TypeElement) ((DeclaredType) field.getEnclosingElement().asType()).asElement()).getQualifiedName().toString();
    }

    static Array<Element> enclosedElements(Element element) {
        if (!isDeclared(element)) {
            return Array.empty();
        } else {
            if (element.getEnclosingElement().toString().equals(((DeclaredType) element.asType()).asElement().toString())) {
                return Array.empty();
            }
            java.util.List<? extends Element> elements = ((DeclaredType)element.asType()).asElement().getEnclosedElements();
            return Array.ofAll(elements.stream().filter(x -> x.getKind() == ElementKind.FIELD));
        }
    }

    static Array<Element> enclosedElements2(DeclaredType type) {
        java.util.List<? extends Element> elements = type.asElement().getEnclosedElements();
        return Array.ofAll(elements.stream().filter(x -> x.getKind() == ElementKind.FIELD));
    }

    boolean isSameAsParent(Element element) {
        if (isDeclared(element)) {
            // the type of the element could potentially be complex, say T<A,B,C>
            String ourType = ((DeclaredType) element.asType()).asElement().toString();
            String ourParent = element.getEnclosingElement().toString();
            return ourType.equals(ourParent);
        }
        return false;
    }

    static boolean isDeclared(Element element) {
        return element.asType().getKind().equals(TypeKind.DECLARED);
    }

    static String typeOf(Element element) {
        return element.asType().toString();
    }

}
