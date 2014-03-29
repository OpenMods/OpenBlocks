package openperipheral.api;

import java.lang.annotation.*;

/**
 * Method marked with this annotation will be called only in main thread and during game tick. Use it on every method that manipulates world.
 * Can be only used on {@link LuaCallable}, since it conflicts with {@link LuaMethod#onTick()}. When used on class level, it will apply to every method, unless method declares it's own annotation
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OnTick {
	boolean value() default true;
}
