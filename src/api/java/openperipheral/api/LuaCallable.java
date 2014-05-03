package openperipheral.api;

import java.lang.annotation.*;

/**
 * Used to mark methods that should be visible in Lua.
 * 
 * @see OnTick
 * @see Arg
 * @see Freeform
 * @see Prefixed
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuaCallable {
	public static final String USE_METHOD_NAME = "[none set]";

	/**
	 * Name visible in Lua. Default will use Java name. More names can be defined with {@link Alias}
	 */
	String name() default USE_METHOD_NAME;

	String description() default "";

	/**
	 * List of types expected to be returned from call. Empty list marks no results. Using {@link LuaType#VOID} in this list will cause error
	 */
	LuaType[] returnTypes() default {};

	/**
	 * Should return values be validated using types from {@link #returnTypes()}
	 */
	boolean validateReturn() default true;
}
