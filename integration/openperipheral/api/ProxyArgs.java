package openperipheral.api;

import java.lang.annotation.*;

/**
 * 
 * @see ProxyArg
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProxyArgs {
	public ProxyArg[] value() default {};
}
