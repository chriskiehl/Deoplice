package deoplice.processor;

import lombok.AllArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Explorer {

//    Function<ElementType, Boolean> recurseWhen() default (new Function<ElementType, Boolean>() {
//        @Override
//        public Boolean apply(ElementType elementType) {
//            return true;
//        }
//    });

    // TODO: we'll need a custom interface here for handling both the
    //       shouldRecurse case as well as the method invocation strategy.
    //       Keeping them separate would allow, say, Looking for a `@With` annotation
    //       while setting a `@Builder` strategy
    Class<? extends Function<Element, Boolean>> foo() default Foo.class;

    @AllArgsConstructor
    class Foo implements Function<Element, Boolean> {
        public Boolean apply(Element e) {
            return true;
        }

        public static Foo f() {
            return new Foo();
        }
    }

    class Bar implements Function<Element, Boolean> {

        @Override
        public Boolean apply(Element element) {
            return null;
        }
    }
}
