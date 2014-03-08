package openperipheral.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface Arg {
	public static final String DEFAULT_NAME = "[none set]";

	String name() default DEFAULT_NAME;

	String description() default "";

	LuaType type();

	boolean isNullable() default false;
}
