package deoplice.processor.types;

import io.vavr.collection.Array;
import lombok.Value;

/**
 * An intermediate representation of the Lens Assignment Statements
 * along with their declaring class.
 *
 * See {@link deoplice.processor.codegen.ClassReconciler} docs for rationale.
 * TL;DR: The 'owning' class for these lenses may be part of a much larger class hierarchy.
 * This intermediate representation is used until we can figure out which AST node to
 * actually attach it to.
 */
@Value
public class LensBundle {
    AST.Qualified declaringClass;
    Array<AST> assignments;
}
