package openperipheral.api.architecture;

import java.lang.annotation.*;

import openperipheral.api.Constants;

/**
 * Used to mark method that shouldn't be visible in some architectures.
 */
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeArchitecture {
	/**
	 * Excluded architectures. See {@link Constants}.
	 */
	public String[] value();
}
