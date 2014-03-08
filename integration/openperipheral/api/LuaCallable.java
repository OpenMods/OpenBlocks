package openperipheral.api;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuaCallable {
	public static final String USE_METHOD_NAME = "[none set]";

	String name() default USE_METHOD_NAME;

	String description() default "";

	LuaType[] returnTypes() default {};

	boolean validateReturn() default true;
}
