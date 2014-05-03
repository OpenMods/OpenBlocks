package openperipheral.api;

import java.lang.annotation.*;

/**
 * Defines names to Java arguments. Alternative to {@link Freeform} and {@link Named}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
public @interface Prefixed {

	public String[] value();
}
