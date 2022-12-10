package example;

import lombok.Value;
import lombok.With;

/**
 * While recursing, we check for self-references within the same object
 * and short-circuit when found in order to prevent infinite recursion.
 * However, in cases like Integer, Constructor, and Class you can still end
 * up in an infinite loop because the references are one hop away from each
 * other. Class -> Constructor -> Class (off into infinity).
 *
 * For all non Lensed annotated classes, this is automatically mitigated by
 * ignoring all children. However, the only real option available for annotated
 * classes is to rely on recursion depth.
 *
 * This shold presumably be rare enough that the general depth counter is Good Enough.
 * However, if a problem, a per-path recursion counter could be introduced.
 *
 * TODO: test this
 */
@With
@Value
public class NestedRecursion {
    int foo = 123;
    int bar = 234;
    ChildClass cls;

    @With
    @Value
    public static class ChildClass {
        String baz = "asdf";
        NestedRecursion x;
    }
}
