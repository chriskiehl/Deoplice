package deoplice.processor.codegen;

import deoplice.processor.types.AST;
import deoplice.processor.types.Extractions;
import io.vavr.API;
import io.vavr.collection.Array;
import io.vavr.collection.Iterator;
import io.vavr.control.Option;
import lombok.val;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static java.lang.String.format;

/**
 * A grab bag of stuff! And things!
 */
public class GrabBag {

    /**
     * Returns the simple (unqualified) name of the element
     */
    public static String simpleName(Element element) {
        return element.getSimpleName().toString();
    }

    public static Array<Element> enclosedElements(Element element) {
        return enclosedElements(element, isKind(ElementKind.FIELD));
    }

    public static Array<Element> enclosedElements(Element element, Predicate<Element> filter) {
        if (!isDeclared(element)) {
            return Array.empty();
        } else {
            // TODO: should this be configurable?
            //       the motivation was for dealing with things like Vavr's Array, which
            //       has an immediate self-reference which makes things recurse up to the
            //       limit. I'm not sure how valuable 10 levels of setBarBarBarBarBar would
            //       be to anyone, though.
            if (element.getEnclosingElement().toString().equals(((DeclaredType) element.asType()).asElement().toString())) {
                return Array.empty();
            }
            java.util.List<? extends Element> elements = ((DeclaredType)element.asType()).asElement().getEnclosedElements();
            return Array.ofAll(elements.stream().filter(filter));
        }
    }

    public static Predicate<Element> isKind(ElementKind kind) {
        return element -> element.getKind().equals(kind);
    }


    public static <A extends Annotation> Option<A > findAnnotation(Element element, Class<A > cls) {
        if (element.getKind().equals(ElementKind.CLASS)) {
            return Option.of(element.getAnnotation(cls));
        } else if (element.asType().getKind().equals(TypeKind.DECLARED)) {
            Element element_ = ((DeclaredType) element.asType()).asElement();
            return Option.of(element_.getAnnotation(cls));
        } else {
            return Option.none();
        }
    }

    public static boolean isDeclared(Element element) {
        return element.asType().getKind().equals(TypeKind.DECLARED);
    }

    /**
     * Produces the fully qualified type as a string.
     *
     * Is `toString()` reliable across JDK implementations? Looks like it.
     * Their respective implementations can be found here:
     *
     *  - Corretto https://tinyurl.com/4bf37jxr
     *  - OpenJDK https://tinyurl.com/bdeutj5f
     *  - Oracle JDK
     *
     * There are minor variations between them, but for our purposes, this is reliable
     * enough and saves a ton of manual AST visiting that would otherwise need to be done.
     */
    public static String typeOf(Element element) {
        TypeMirror type = element.asType();
        return type.getKind().isPrimitive()
                // we have to "auto-box" the primitive types to their object equivalents
                // as generics don't work with the primitive types
                ? boxedPrimitives.get(type.getKind())
                : type.toString();
    }

    static final Map<TypeKind, String> boxedPrimitives = new java.util.HashMap<>(){{
        put(TypeKind.BOOLEAN, "java.lang.Boolean");
        put(TypeKind.BYTE, "java.lang.Byte");
        put(TypeKind.SHORT, "java.lang.Short");
        put(TypeKind.INT, "java.lang.Integer");
        put(TypeKind.LONG, "java.lang.Long");
        put(TypeKind.CHAR, "java.lang.Character");
        put(TypeKind.FLOAT, "java.lang.Float");
        put(TypeKind.DOUBLE, "java.lang.Double");
    }};

    /**
     * The raw (non-parameterized) type. e.g.
     * Given `java.util.List<String>`
     * This would return `java.util.List`
     */
    public static String rawTypeOf(Element element) {
        String s = typeOf(element);
        int idx = s.indexOf("<");
        if (idx > 0) {
            return s.substring(0, idx);
        } else {
            return s;
        }
    }

    /**
     * The qualified type of the first generic type parameter. e.g.
     *
     * java.util.List<java.lang.String> -> java.lang.String
     */
    public static String genericTypeOf(Element element) {
        if (!element.asType().getKind().equals(TypeKind.DECLARED)
                || (element.asType().getKind().equals(TypeKind.DECLARED) && ((DeclaredType) element.asType()).getTypeArguments().size() != 1)) {
            throw new RuntimeException("Programmer error! You're attempting to type parameter of a non-generic single arg type!");
        }
        return ((DeclaredType) element.asType()).getTypeArguments().get(0).toString();
    }


    public static AST.Qualified qualifiedName(AST node) {
        return API.Match(node).of(
                Case($(instanceOf(AST.ClassDef.class)), cls -> null),
                Case($(instanceOf(AST.MethodDef.class)), cls -> null),
                Case($(instanceOf(AST.LensAssignment.class)), x -> AST.Qualified.of(x.getDeclaringClass().getValue() + "." + x.getName()))
        );
    }

    public static String pkg(Element e) {
        Element current = e;
        while (current.getKind() != ElementKind.PACKAGE) {
            current = current.getEnclosingElement();
        }
        return current.toString();
    }

    public static String pkg(AST.Qualified name) {
        return pkg(name.getValue());
    }

    public static String pkg(String possiblyQualifiedName) {
        char[] xs = possiblyQualifiedName.toCharArray();
        int end = 0;
        // For cases where there is no package name and we're
        // starting with the class name.
        if (Character.isUpperCase(xs[end])) {
            return String.valueOf(xs);
        }
        while (end < xs.length) {
            // we're going on the naive (but pretty safe) bet that package names
            // are lowercase and Class names are TitleCase. So, here, this check here
            // is detecting that first uppercase letter denoting the start of a class name.
            if (Character.isUpperCase(xs[end])) {
                end--;
                break;
            }
            end++;
        }
        return String.valueOf(Arrays.copyOfRange(xs, 0, end));
    }


    public static String lensifyDeclaringClass(Element e) {
        return lensifyDeclaringClass(e, "Lens");
    }

    public static String lensifyDeclaringClass(Element e, String suffix) {
        if (e.getKind() == ElementKind.FIELD) {
            return lensifyDeclaringClass(e.getEnclosingElement(), suffix);
        } else if (e.getKind() == ElementKind.PACKAGE) {
            return e.toString();
        } else {
            return lensifyDeclaringClass(e.getEnclosingElement(), suffix) + "." + (e.getSimpleName() + suffix);
        }
    }

    public static Array<String> splitDeclaringClasses(Element e) {
        if (e.getKind() == ElementKind.FIELD) {
            return splitDeclaringClasses(e.getEnclosingElement());
        } else if (e.getEnclosingElement().getKind() == ElementKind.PACKAGE) {
            return Array.of(e.toString());
        } else {
            return splitDeclaringClasses(e.getEnclosingElement()).append(e.getSimpleName().toString());
        }
    }

    /**
     * Split a potentially compound qualified name into its individual classes.
     * The thing that requires care here is that nested inner classes are Simple Names
     * that are chained off the owning class, which is fully qualified. So, effectively, this
     * this splits things into a fully qualified root, with unqualified inner classes.
     * e.g.
     *      "foo.bar.Baz.SomeInner.DeeplyNested"
     * ==>
     *      ["foo.bar.Baz", "SomeInner", "DeeplyNested"]
     */
    public static Array<String> splitDeclaringClasses2(String fullyQualifiedPath) {
        char[] xs = fullyQualifiedPath.toCharArray();

        Array<String> output = Array.empty();
        int start = 0;
        int end = 0;
        while (end < xs.length) {
            // we're going on the naive (but pretty safe) bet that package names
            // are lowercase and Class names are TitleCase. So, here, this check here
            // is detecting that first uppercase letter denoting the start of a class name.
            if (Character.isUpperCase(xs[end])) {
                // then we just advance the pointer to the next "."
                while (end < xs.length && xs[end] != '.') {
                    end++;
                }
                // and Bob's your uncle.
                output = output.append(String.valueOf(Arrays.copyOfRange(xs, start, end)));
                // skip the next period.
                start = end + 1;
            }
            end++;
        }
        return output;
    }


    /**
     * Grabs that package name (sorta).
     *
     * This is purposefully does the Wrong Thing when processing
     * nested classes. It included the parent class in the package
     * name, which is just a convenient and lazy way to avoid collisions
     * that otherwise may occur.
     */
    public static String packageish(String s) {
        int index = s.lastIndexOf(".");
        return index > -1
                ? s.substring(0, index).toLowerCase(Locale.ROOT)
                : s.toLowerCase(Locale.ROOT);
    }

    public static String packageish(AST.Qualified qualifiedName) {
        return packageish(qualifiedName.getValue());
    }


    public static String titleCase(String s) {
        return Character.toTitleCase(s.charAt(0)) + s.substring(1);
    }


    public static String unqualify(AST.Qualified qualifiedType) {
        // TODO: is + 1 safe?
        val str = qualifiedType.getValue();
        int index = str.lastIndexOf(".");
        return index > -1
                ? str.substring(str.lastIndexOf(".")).replace(".", "")
                : str;
    }

    public static String fn(String parameterType) {
        return format("java.util.function.Function<%s, %s>", parameterType, parameterType);
    }

    public static String predicate(String parameterType) {
        return format("java.util.function.Predicate<%s>", parameterType);
    }

    /**
     * A standard sole argument param called "param1"
     */
    public static Array<AST.Parameter> standardParam(String type) {
        return Array.of(new AST.Parameter(AST.Qualified.of(type), "param1"));
    }

    /**
     * The root of a (potentially) deeply nested POJO hierarchy.
     * Given
     * ```
     * class Foo {
     *     Bar bar
     * }
     * class Bar {
     *     Baz baz    <- processing this guy
     * }
     * class Baz {...}
     * ```
     * this would return the root owner: `Foo`
     */
    public static AST.Qualified rootClass(Extractions.ExtractedField field) {
        return AST.Qualified.of(field.getLineage()
                .headOption().map(x -> declaringClass(x.getElement()))
                .getOrElse(declaringClass(field.getElement())));
    }

    /**
     * API method names are build from concatenating each level in the hierarchy
     * starting from the outermost.
     */
    static String nestedVariableName(String prefix, Extractions.ExtractedField field) {
        return format("%s%s%s",
                prefix,
                field.getLineage().map(x -> titleCase(simpleName(x.getElement()))).mkString(),
                titleCase(simpleName(field.getElement()))
        );
    }

    public static String declaringClass(Element field) {
        return ((TypeElement) ((DeclaredType) field.getEnclosingElement().asType()).asElement()).getQualifiedName().toString();
    }


    public static Array<String> standardImports = Array.of(
            "import deoplice.lenses.Lens;",
            "import java.util.function.Function;",
            "import static deoplice.lenses.API.makeLens;"
    );
}
