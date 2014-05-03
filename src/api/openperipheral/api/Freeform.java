package openperipheral.api;

import java.lang.annotation.*;

/**
 * Annotation used to mark methods that don't use prefixed Java argument names. Every argument must be named with {@link Named} or registration will fail.
 * When used on class level, it will apply to every method, unless method declares it's own annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Freeform {
	boolean value() default true;
}
