package openperipheral.api;

import java.lang.reflect.Method;

public class IntegrationRegistry {

	public boolean registerAdapter(IPeripheralAdapter adapter) {
		return register(IPeripheralAdapter.class, adapter, "openperipheral.core.AdapterManager", "addPeripheralAdapter");
	}

	public boolean registerTypeConverter(ITypeConverter converter) {
		return register(ITypeConverter.class, converter, "openperipheral.core.TypeConversionRegistry", "registerTypeConverter");
	}

	public boolean registerRobotUpgradeProvider(IRobotUpgradeProvider provider) {
		return register(IRobotUpgradeProvider.class, provider, "openperipheral.robot.RobotUpgradeManager", "registerUpgradeProvider");
	}

	private boolean register(Class type, Object obj, String klazzName, String methodName) {
		try {
			Class klazz = Class.forName(klazzName);
			if (klazz != null) {
				Method method = klazz.getMethod(methodName, new Class[] { type });
				method.invoke(null, obj);
				return true;
			}
		} catch (Exception e) {}
		return false;
	}
}
