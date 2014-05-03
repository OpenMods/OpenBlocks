package openperipheral.api;

import java.lang.annotation.*;

/**
 * This annotation allows to specify additional Lua names for method (alongside {@link LuaMethod#name()} and {@link LuaCallable#name()}.
 * Those names will be visible as additional entries, but they will all point to same method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
	public String[] value();
}
