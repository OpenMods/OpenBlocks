package openperipheral.api.cc15x;

import openperipheral.api.APIHelpers;

/**
 * This class contains calls that use CC1.5X specific classes. Always use absolute class names here to prevent accidental use of newer API
 */
public class ComputerCraftWrappers {
	private static final String IMPLEMENTATION = "openperipheral.adapter.WrappersCC15X";

	public static dan200.computer.api.ILuaObject createWrapper(Object obj) {
		return APIHelpers.callWithReturn(IMPLEMENTATION, "createObjectWrapper", Object.class, obj, dan200.computer.api.ILuaObject.class);
	}

	public static dan200.computer.api.IHostedPeripheral createHostedPeripheral(Object target) {
		return APIHelpers.callWithReturn(IMPLEMENTATION, "createHostedPeripheral", Object.class, target, dan200.computer.api.IHostedPeripheral.class);
	}
}
