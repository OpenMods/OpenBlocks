package openperipheral.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

	public LuaType type() default LuaType.OBJECT;

	public String name() default "";

	public String getterDesc() default "";

	public String setterDesc() default "";

	public boolean readOnly() default false;
}
