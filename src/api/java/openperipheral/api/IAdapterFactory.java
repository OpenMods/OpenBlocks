package openperipheral.api;

import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.peripheral.IPeripheral;

public interface IAdapterFactory extends IApiInterface {
	public ILuaObject wrapObject(Object target);

	public IPeripheral createPeripheral(Object target);
}
