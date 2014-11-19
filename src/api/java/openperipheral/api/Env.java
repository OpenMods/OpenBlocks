package openperipheral.api;

import java.lang.annotation.*;

/**
 *
 * Used to mark method arguments as receivers of extra instanceof of env variables
 * (like {@code "computer"} -> {@link dan200.computercraft.api.peripheral.IComputerAccess} and {@code "context"} -> {@link dan200.computercraft.api.lua.ILuaContext}).
 * Available variable names depend on context.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Env {
	public String value();
}
