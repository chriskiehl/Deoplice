package deoplice.processor;

import deoplice.processor.codegen.WithStrategy;

import javax.lang.model.element.Element;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Updatable {
    String[] exclude() default {};

    // Fundamental limits on how much we can pass to the
    // annotation in terms of strategy ergonomics:
    // https://docs.oracle.com/javase/specs/jls/se19/html/jls-9.html#jls-9.6.2
    // Looks like we'll have to do something like Lombok's @ExtensionMethod and
    // have a bespoke class passed in.
    // Hours later, (what should have been obvious), there's no way of using a client's
    // class directly -- it's not even compiled yet! That's the whole point...
    // Lombok is doing some deep, dark, evil magic in order to inject the class into the
    // AST: https://github.com/projectlombok/lombok/blob/9be867ef03b77e0455a45d22e4a8cd7c5fa9af61/src/core/lombok/javac/handlers/HandleExtensionMethod.java#L194
    // this is so that it become part of the call path at runtime.
    // AFAICT, there's no way for a user to supply executable code to the annotation processor.
    //
    // CONFIRMED: https://stackoverflow.com/questions/74677159/is-it-posible-to-supply-executable-code-to-an-annotation-for-use-in-the-processi
    // There is no way for client code to supply anything that runs at annotation processing time.
    // They're different compilation stages.
    // So, this should be something like...
    // ENUM {@WITH, @BUILDER, METHOD_PREFIX={getterPrefix=, setterPrefix=}}
    // where the later looks for explicit getters/setters on the classes as a last ditch effort.
    Class<? extends Function<Element, Boolean>> recursionStrat() default WithStrategy.class;
}
