package openperipheral.api.cc16;

import openperipheral.api.APIHelpers;

/**
 * This class contains calls that use CC1.6 specific classes. Always use absolute class names here to prevent accidental use of older API
 */
public class ComputerCraftWrappers {
	private static final String IMPLEMENTATION = "openperipheral.adapter.WrappersCC16";

	public static dan200.computercraft.api.lua.ILuaObject createWrapper(Object obj) {
		return APIHelpers.callWithReturn(IMPLEMENTATION, "wrapObject", Object.class, obj, dan200.computercraft.api.lua.ILuaObject.class);
	}

	public static dan200.computercraft.api.peripheral.IPeripheral createPeripheral(Object target) {
		return APIHelpers.callWithReturn(IMPLEMENTATION, "createPeripheral", Object.class, target, dan200.computercraft.api.peripheral.IPeripheral.class);
	}
}
