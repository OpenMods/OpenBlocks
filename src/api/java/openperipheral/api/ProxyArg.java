package openperipheral.api;

import java.lang.annotation.*;

/**
 *
 * Methods marked with this annotation will get instance of {@link IMethodProxy} in one of names Java arguments.
 * Proxy will point to method of target class that is named with one of values from {@link #argName()} and has argument types {@link #args()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProxyArg {

	/**
	 * Name of target argument. Use {@link Named} to mark which one
	 */
	public String argName() default "proxy";

	/**
	 * Names of proxied method from target class. If default value is left, it will use name of marked method
	 */
	public String[] methodNames() default {};

	/**
	 * Argument of proxied method from target class. If default value is left, it will use Lua argument of marked method
	 */
	public Class<?>[] args() default { void.class };
}
