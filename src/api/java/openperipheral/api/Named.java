package openperipheral.api;

import java.lang.annotation.*;

/**
 *
 * Used to name Java part of arguments in methods annotated with {@link Freeform}. Values depend on adapter type and location
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Named {
	public String value();
}
