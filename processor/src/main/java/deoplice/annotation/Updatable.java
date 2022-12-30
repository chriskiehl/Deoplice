package deoplice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Updatable {
    String lensVariablePrefix() default "$";
    String lensClassSuffix() default "Lens";
    String apiClassSuffix() default "API";
    String apiSetterPrefix() default "set";
    String apiUpdaterPrefix() default "update";

    int maxRecursionDepth() default 10;
}
