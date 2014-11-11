package openperipheral.api;

import java.lang.annotation.*;

/**
 * This annotation is used to mark class fields that should be exposed in Lua as get/set accessors.
 * Class that uses this annotation must implement {@link IPropertyCallback}, otherwise registration will fail.
 * Every call to accessors will be passed to callback
 * 
 * @see Property
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CallbackProperty {

	/**
	 * Type of setter parameter and getter result. Used only for documentation
	 */
	public LuaArgType type() default LuaArgType.AUTO;

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
