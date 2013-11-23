package openperipheral.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuaMethod {
	boolean onTick() default true;
	String name() default "[none set]";
	String description() default "";
	LuaType returnType() default LuaType.VOID;
	Arg[] args() default {};
}