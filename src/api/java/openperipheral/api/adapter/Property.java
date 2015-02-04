package openperipheral.api.adapter;

import java.lang.annotation.*;

import openperipheral.api.adapter.method.ArgType;

/**
 * This annotation is used to mark class fields that should be exposed in Lua as get/set accessors.
 * Every call will directly operate on field.
 *
 * @see CallbackProperty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

	/**
	 * Type of setter parameter and getter result. Used only for documentation
	 */
	public ArgType type() default ArgType.AUTO;

	/**
	 * Field name used for naming get/set methods. If empty, original field name will be used.
	 * First letter of name be capitalized, therefore for value {@code XyzZyx} accessors will be named {@code getXyzZyx} and {@code setXyzZyx}
	 */
	public String name() default "";

	/**
	 * Short description of getter
	 */
	public String getterDesc() default "";

	/**
	 * Short description of setter
	 */
	public String setterDesc() default "";

	/**
	 * If true, only getter will be generated
	 */
	public boolean readOnly() default false;
}
