package openperipheral.api;

import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IPeripheral;

/**
 * API interface for wrapping Java objects to ComputerCraft structures.
 */
public interface IAdapterFactory extends IApiInterface {
	public ILuaObject wrapObject(Object target);

	public IPeripheral createPeripheral(Object target);
}
