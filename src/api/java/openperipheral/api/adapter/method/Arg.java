package openperipheral.api.adapter.method;

import java.lang.annotation.*;

/**
 * This annotation is used to supply metadata about arguments in script methods.
 * Every argument that is supposed to be filled by caller in script should be marked with this one.
 *
 * @see Env
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arg {
	/**
	 * This name will be visible in documentation program or in {@code .listMethods()} result.
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
	ArgType type() default ArgType.AUTO;

	/**
	 * If this value is true, argument accepts {@code null} values ({@code nil} on scripting side).
	 * When nullable values are allowed, Java type of argument must not be primitive.
	 */
	boolean isNullable() default false;
}
