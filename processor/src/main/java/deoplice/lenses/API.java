package deoplice.lenses;


import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A minimal Lens API.
 */
public class API {

    /**
     * Convenience method for creating a new Lens from a fixed getter / setter.
     */
    public static <A,B> Lens<A,B> makeLens(Function<A,B> getter, BiFunction<A,B, A> setter) {
        return new Lens<A, B>() {
            @Override
            public B get(A a) {
                return getter.apply(a);
            }

            @Override
            public A set(A a, B b) {
                return setter.apply(a, b);
            }
        };
    }

    /**
     * Creates a composable function which will set the object to the supplied value when applied.
     */
    public static <A, B> Function<A, A> set(Lens<A,B> lens, B value) {
        return update(lens, _const(value));
    }
    /**
     * Creates a composable function which will set the object to the supplied value when applied.
     */
    public static <A, B, C> Function<A, A> set(Lens<A,B> a, Lens<B, C> b, C value) {
        return update(a, b, _const(value));
    }
    /**
     * Creates a composable function which will set the object to the supplied value when applied.
     */
    public static <A, B, C, D> Function<A, A> set(Lens<A,B> a, Lens<B, C> b, Lens<C, D> c, D value) {
        return update(a, b, c, _const(value));
    }
    /**
     * Creates a composable function which will set the object to the supplied value when applied.
     */
    public static <A, B, C, D, E> Function<A, A> set(Lens<A,B> a, Lens<B, C> b, Lens<C, D> c, Lens<D,E> d, E value) {
        return update(a, b, c, d, _const(value));
    }
    /**
     * Creates a composable function which will set the object to the supplied value when applied.
     */
    public static <A, B, C, D, E, F> Function<A, A> set(Lens<A,B> lens1, Lens<B,C> lens2, Lens<C,D> lens3, Lens<D,E> lens4, Lens<E,F> lens5, F value) {
        return update(lens1, lens2, lens3, lens4, lens5, _const(value));
    }
    /**
     * Creates a composable function which will apply the supplied mapping function when later applied.
     */
    public static <A, B> Function<A, A> update(Lens<A,B> lens, Function<B, B> f) {
        return (A obj) -> lens.set(obj, f.apply(lens.get(obj)));
    }
    /**
     * Creates a composable function which will apply the supplied mapping function when later applied.
     */
    public static <A, B, C> Function<A, A> update(Lens<A,B> lens1, Lens<B,C> lens2, Function<C, C> f) {
        return update(lens1.compose(lens2), f);
    }
    /**
     * Creates a composable function which will apply the supplied mapping function when later applied.
     */
    public static <A, B, C, D> Function<A, A> update(Lens<A,B> lens1, Lens<B,C> lens2, Lens<C,D> lens3, Function<D, D> f) {
        return update(lens1.compose(lens2).compose(lens3), f);
    }
    /**
     * Creates a composable function which will apply the supplied mapping function when later applied.
     */
    public static <A, B, C, D, E> Function<A, A> update(Lens<A,B> lens1, Lens<B,C> lens2, Lens<C,D> lens3, Lens<D,E> lens4, Function<E, E> f) {
        return update(lens1.compose(lens2).compose(lens3).compose(lens4), f);
    }
    /**
     * Creates a composable function which will apply the supplied mapping function when later applied.
     */
    public static <A, B, C, D, E, F> Function<A, A> update(Lens<A,B> lens1, Lens<B,C> lens2, Lens<C,D> lens3, Lens<D,E> lens4, Lens<E,F> lens5,Function<F, F> f) {
        return update(lens1.compose(lens2).compose(lens3).compose(lens4).compose(lens5), f);
    }

    private static <A> Function<A, A> _const(A a) {
        return (A __) -> a;
    }
}
