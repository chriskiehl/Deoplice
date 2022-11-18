package deoplice.lenses;

public interface Lens<A, B> {
    /**
     * A plain ol' getter.
     */
    B get(A a);

    /**
     * An immutable "setter". Generally Lombok's auto-generated @With
     * is a good candidate for this chap.
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
