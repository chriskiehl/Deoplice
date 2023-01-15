package deoplice.processor.codegen;

import deoplice.processor.types.AST;
import deoplice.processor.types.Extractions;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import lombok.val;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

import static java.lang.String.format;

/**
 * A grab bag of things! And stuff!
 */
public class GrabBag {



    /**
     * Converts the supplied string to TitleCase.
     * e.g. fooBar -> FooBar
     */
    public static String titleCase(String s) {
        return Character.toTitleCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Returns the unqualified (simple) name from a qualified one.
     * e.g. "foo.bar.Baz" -> "Baz"
     * When a nested qualfiied type (i.e. inner class) it returns the
     * final class.
     * e.g. "foo.bar.Baz.Bazzer" -> "Bazzer"
     */
    public static String unqualify(AST.Qualified qualifiedType) {
        // TODO: is + 1 safe?
        val str = qualifiedType.getValue();
        int index = str.lastIndexOf(".");
        return index > -1
                ? str.substring(str.lastIndexOf(".")).replace(".", "")
                : str;
    }

    /**
     * Returns the simple (unqualified) name of the element
     */
    public static String unqualifiedName(Element element) {
        return element.getSimpleName().toString();
    }

    /**
     * Builds the fully qualified Name for the supplied lens.
     */
    public static AST.Qualified qualifiedName(AST.LensAssignment assignment) {
        return AST.Qualified.of(assignment.getDeclaringClass().getValue() + "." + assignment.getName());
    }

    /**
     * Returns all children of the supplied element which are of type FIELD.
     */
    public static Array<Element> enclosedFields(Element element) {
        return enclosedFields(element, isKind(ElementKind.FIELD).and(GrabBag::hasNoTypeVarsOrWildcards));
    }

    /**
     * Returns all children of the supplied element which are of type FIELD and satisfy
     * the supplied predicate.
     */
    public static Array<Element> enclosedFields(Element element, Predicate<Element> filter) {
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

    public static boolean isDeclared(Element element) {
        return element.asType().getKind().equals(TypeKind.DECLARED);
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


    /**
     * Produces the fully qualified type as a string.
     *
     * Is `toString()` reliable across JDK implementations? Looks like it.
     * Their respective implementations can be found here:
     *
     *  - Corretto: https://tinyurl.com/4bf37jxr
     *  - OpenJDK: https://tinyurl.com/bdeutj5f
     *  - Oracle JDKL:
     *
     * There are minor variations between them, but for our purposes, this is reliable
     * enough and saves a ton of manual AST visiting that would otherwise need to be done
     * in order to resolve the full, potentially complex, type signature.
     */
    public static String typeOf(Element element) {
        TypeMirror type = element.asType();
        return type.getKind().isPrimitive()
                // we have to "auto-box" the primitive types to their object equivalents
                // as generics don't work with the primitive types
                ? boxedPrimitives.get(type.getKind())
                : type.toString();
    }

    /**
     * Autoboxing rules.
     */
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
     * Given {@literal `java.util.List<String>`}
     * This would return {@literal `java.util.List`}
     */
    public static String nonParameterizedType(Element element) {
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
     * {@literal java.util.List<java.lang.String> -> java.lang.String}
     * TODO: eventually I'll handle types with more complex signatures.
     */
    public static String firstTypeParameter(Element element) {
        if (!element.asType().getKind().equals(TypeKind.DECLARED)
                || (element.asType().getKind().equals(TypeKind.DECLARED) && ((DeclaredType) element.asType()).getTypeArguments().size() != 1)) {
            throw new RuntimeException("Programmer error! You're attempting to type parameter of a non-generic single arg type!");
        }
        return ((DeclaredType) element.asType()).getTypeArguments().get(0).toString();
    }

    /**
     * Checks if all generic parameters are of a declared
     */
    public static boolean hasNoTypeVarsOrWildcards(Element element) {
        return hasNoTypeVarsOrWildcards(element.asType());
    }
    public static boolean hasNoTypeVarsOrWildcards(TypeMirror type) {
        if (type.getKind().equals(TypeKind.DECLARED)) {
            boolean noWildcards = true;
            for (TypeMirror type2 : ((DeclaredType) type).getTypeArguments()) {
                noWildcards = noWildcards && hasNoTypeVarsOrWildcards(type2);
            }
            return noWildcards;
        }
        return !(type.getKind().equals(TypeKind.WILDCARD) || type.getKind().equals(TypeKind.TYPEVAR));
    }

    /**
     * Makes a best guess at extracting the package from a
     * potentially qualified name.
     * e.g.
     * ```
     * pkg("java.lang.String") == "java.lang"
     * ```
     */
    public static String pkg(AST.Qualified name) {
        return pkg(name.getValue());
    }

    /**
     * Makes a best guess at extracting the package from a
     * potentially qualified name.
     * e.g.
     * ```
     * pkg("java.lang.String") == "java.lang"
     * ```
     */
    public static String pkg(String possiblyQualifiedName) {
        char[] xs = possiblyQualifiedName.toCharArray();
        int end = 0;
        // For cases where there is no package name and we're
        // starting with the class name.
        if (Character.isUpperCase(xs[end])) {
            return ""; //String.valueOf(xs);
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

    /**
     * Remaps all Classes in the Element's hierarchy to their Deoplice Suffixed
     * equivalents. e.g.
     * ```
     * "foo.bar.Bar.Baz" -> "foo.bar.BarLens.BazLens"
     * ```
     */
    public static String lensifyDeclaringClass(Element e, String suffix) {
        if (e.getKind() == ElementKind.FIELD) {
            return lensifyDeclaringClass(e.getEnclosingElement(), suffix);
        } else if (e.getKind() == ElementKind.PACKAGE) {
            return e.toString();
        } else {
            return lensifyDeclaringClass(e.getEnclosingElement(), suffix) + "." + (e.getSimpleName() + suffix);
        }
    }


    /**
     * Split a potentially compound qualified name into its individual classes.
     *
     * The thing that requires care here is that nested inner classes are Simple Names
     * that are chained off the owning class, which is fully qualified. So, effectively, this
     * this splits things into a fully qualified root, with unqualified inner classes.
     * e.g.
     *      "foo.bar.Baz.SomeInner.DeeplyNested"
     * ==>
     *      ["foo.bar.Baz", "SomeInner", "DeeplyNested"]
     */
    public static Array<String> splitDeclaringClasses2(AST.Qualified fullyQualifiedPath) {
        char[] xs = fullyQualifiedPath.getValue().toCharArray();

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



    public static String fn(String parameterType) {
        return fn(parameterType, parameterType);
    }

    public static String fn(String inputType, String returnType) {
        return format("java.util.function.Function<%s, %s>", inputType, returnType);
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
     * {@literal
     * ```
     * class Foo {
     *     Bar bar
     * }
     * class Bar {
     *     Baz baz    <- processing this guy
     * }
     * class Baz {...}
     * ```
     * }
     * this would return the root owner: `Foo`
     */
    public static AST.Qualified rootClass(Extractions.ExtractedField field) {
        return AST.Qualified.of(field.getLineage()
                .headOption().map(x -> declaringClass(x.getElement()))
                .getOrElse(declaringClass(field.getElement())));
    }

    public static Element rootElement(Extractions.ExtractedField field) {
        return field.getLineage().headOption().map(x -> x.getElement().getEnclosingElement())
                .getOrElse(field.getElement().getEnclosingElement());
    }

    /**
     * API method names are build from concatenating each level in the hierarchy
     * starting from the outermost.
     */
    static String nestedVariableName(String prefix, Extractions.ExtractedField field) {
        return format("%s%s%s",
                prefix,
                field.getLineage().map(x -> titleCase(unqualifiedName(x.getElement()))).mkString(),
                titleCase(unqualifiedName(field.getElement()))
        );
    }

    public static String declaringClass(Element field) {
        return ((TypeElement) ((DeclaredType) field.getEnclosingElement().asType()).asElement()).getQualifiedName().toString();
    }

    public static String declaringType(Element field) {
        TypeMirror type = field.getEnclosingElement().asType();
        if (type.getKind().equals(TypeKind.DECLARED)) {
            String stringlyType = type.toString();
            for (TypeMirror typeMirror : ((DeclaredType) type).getTypeArguments()) {
                if (typeMirror.getKind().equals(TypeKind.TYPEVAR)) {
                    stringlyType = stringlyType.replace(typeMirror.toString(), "?");
                }
            }
            return stringlyType;
        } else {
            return type.toString();
        }
    }

    public static String generifiedType(Element field) {
        TypeMirror type = field.asType();
        if (type.getKind().equals(TypeKind.DECLARED)) {
            String stringlyType = type.toString();
            for (TypeMirror typeMirror : ((DeclaredType) type).getTypeArguments()) {
                if (typeMirror.getKind().equals(TypeKind.TYPEVAR)) {
                    stringlyType = stringlyType.replace(typeMirror.toString(), "?");
                }
            }
            return stringlyType;
        } else {
            return type.toString();
        }
    }

    public static Array<String> standardImports = Array.of(
            "import deoplice.lenses.Lens;",
            "import java.util.function.Function;",
            "import static deoplice.lenses.API.makeLens;"
    );
}
