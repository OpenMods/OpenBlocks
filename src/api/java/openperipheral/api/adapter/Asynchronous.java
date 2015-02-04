package openperipheral.api.adapter;

import java.lang.annotation.*;

/**
 * Method marked with this annotation will be called inside computer thread (instead of beeing called inside main thread, after world tick).
 * When used on class level, it will apply to every method, unless method declares it's own annotation.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Asynchronous {
	boolean value() default true;
}
