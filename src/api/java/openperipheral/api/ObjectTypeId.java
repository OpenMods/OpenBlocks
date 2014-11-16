package openperipheral.api;

import java.lang.annotation.*;

import dan200.computercraft.api.lua.ILuaObject;

/**
 * Used for creating custom names for generated {@link ILuaObject} and source id of generated adapters
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectTypeId {
	public String value();
}
