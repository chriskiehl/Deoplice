package deoplice.processor;

import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Explore {

    @Test
    public void adfads2fff() {
        FieldTypes<Object> ft = FieldTypes.builder()
                .build();
    }


    @Test
    public void adfadsf() {
        Root x = new Root(new Foo("Bob"));
        System.out.println($root$bar.set(x, "FOFOFOFOF"));
        System.out.println($id$root$bar.set(x, "FOFOFOFOF"));
    }

    @With
    @Value
    public static class Root {
        Foo foo;
    }

    @With
    @Value
    public static class Foo {
        String bar;
    }

    Lens<Root, Root> $identity = makeLens(x -> x, (y, y_) -> y_);
    Lens<Root, Foo> $foo = makeLens(Root::getFoo, Root::withFoo);
    Lens<Foo, String> $bar = makeLens(Foo::getBar, Foo::withBar);
    Lens<Root, String> $root$bar = $foo.compose($bar); //   $identity.compose($foo).compose($bar);
    Lens<Root, String> $id$root$bar = $identity.compose($foo).compose($bar);



    static <A,B> Lens<A, B> makeLens(Function<A, B> getter, BiFunction<A, B, A> setter) {
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


    interface Lens<A, B> {
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
         */
        A set(A a, B b);

        /**
         * Composes two Lenses together into a new lens.
         * This works just like function composition. Meaning, if you've
         * got `A -> B` and compose it with a `B -> C` you get back a
         * new function `A -> C`.
         * <p>
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


}
