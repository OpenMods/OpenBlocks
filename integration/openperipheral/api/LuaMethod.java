package openperipheral.api;

import java.lang.annotation.*;

/**
 * Used to mark methods that should be visible in Lua
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuaMethod {
	public static final String USE_METHOD_NAME = "[none set]";

	/**
	 * Should method be called only in main thread and during game tick. Set it to true on every method that manipulates world
	 */
	boolean onTick() default true;

	/**
	 * Name visible in Lua. Default will use Java name. More names can be defined with {@link Alias}
	 */
	String name() default USE_METHOD_NAME;

	String description() default "";

	/**
	 * Return type used for documentation. Not validated!
	 */
	LuaType returnType() default LuaType.VOID;

	/**
	 * Metadata about Lua arguments
	 */
	Arg[] args() default {};
}