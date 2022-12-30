package deoplice.processor.types;

import deoplice.lenses.API;
import deoplice.lenses.Lens;
import io.vavr.collection.Array;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.function.Function;

/**
 * A very loose "AST" modeling the various java classes, methods, and
 * assignments that will be created. It's a bit of a frankenstein mix of
 * data types and stringly code representations.
 */
public interface AST {

    @Value @Builder @With class ClassDef implements AST {
        String pkg;
        Array<String> imports;
        String name;
        Array<AST> body;
    }

    @Value @Builder @With class InnerClassDef implements AST {
        String name;
        Array<AST> body;
    }

    @Value @Builder class MethodDef implements AST {
        String name;
        Qualified declaringClass;
        Array<Parameter> params;
        String body;
    }
    @Value @Builder class LensAssignment implements AST {
        String name;
        Qualified declaringClass;
        String assignment;
    }

    @Value @Builder class ApiMethodDef {
        String action;
        Array<Parameter> params;
        String expression;
    }

    @Value(staticConstructor = "of")
    class LensRef {
        Array<Qualified> lenses;
    }

    @Value class Parameter {
        Qualified type;
        String name;
    }

    /**
     * A fully qualified name or type.
     * e.g. "java.lang.String" rather than just "String"
     */
    @Value(staticConstructor = "of")
    class Qualified {
        String value;
    }
}
