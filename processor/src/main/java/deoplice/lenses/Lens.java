package deoplice.lenses;

public interface Lens<A, B> {
    /**
     * A plain ol' getter. By default, Deoplice will assume all setters
     * are prefixed with `get` in the usual camelCased fashion. This can be
     * overridden via
     * TODO: docs
     */
    B get(A a);

    /**
     * TODO: docs
     * An immutable "setter". By default, Deoplpice will assume Lombok's
     * auto-generated @With is available and will
     * is a good candidate for this. `toBuilder()` is another good option.
     *
     */
    A set(A a, B b);

    /**
     * Composes two Lenses together into a new lens.
     * This works just like function composition. Meaning, if you've
     * got `A -> B` and compose it with a `B -> C` you get back a
     * new function `A -> C`.
     *
     * This is subtly awesome. It means your getters/setters are now lego bricks.
     * They can be mixed/matched/combined to work with arbitrarily deep or nested
     * data structures! Lenses are "Exhibit A" for the power of compositional APIs!
     */
    default <C> Lens<A, C> compose(Lens<B, C> inner) {
        Lens<A, B> outer = this;
        return new Lens<A, C>() {
            @Override
            public C get(A a) {
                return inner.get(outer.get(a));
            }

            @Override
            public A set(A a, C c) {
                return outer.set(a, inner.set(outer.get(a), c));
            }
        };
    }
}