package openperipheral.api;

import java.lang.annotation.*;

/**
 * This annotation is used to supply metadata about arguments in Lua methods.
 * Every Lua argument should be marked with this one, either through {@link LuaMethod#args()} or directly on argument
 * when using {@link LuaCallable}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arg {
	/**
	 * This name will be visible in documentation program or in .listMethods() result.
	 * Value is mandatory, since information about argument name is not visible in runtime.
	 * This value has no effects in Java part
	 */
	String name();

	/**
	 * Short description, displayed by documentation program.
	 * This value is not used for validation and calling
	 */
	String description() default "";

	/**
	 * Hint about type of value, displayed by documentation program.
	 * This value is not used for validation, but please keep it correct
	 */
	LuaArgType type() default LuaArgType.AUTO;

	/**
	 * If this value is true, argument accepts {@code null} values ({@code nil} on Lua side).
	 * When nullable values are allowed, Java type of argument must not be primitive.
	 */
	boolean isNullable() default false;
}
