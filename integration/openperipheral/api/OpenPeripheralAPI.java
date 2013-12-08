package openperipheral.api;

import java.lang.reflect.Method;

public class OpenPeripheralAPI {

	public static boolean register(IPeripheralAdapter adapter) {
		return register(IPeripheralAdapter.class, adapter, "openperipheral.AdapterManager", "addPeripheralAdapter");
	}

	public static boolean register(ITypeConverter converter) {
		return register(ITypeConverter.class, converter, "openperipheral.TypeConversionRegistry", "registerTypeConverter");
	}

	public static boolean register(IIntegrationModule module) {
		return register(IIntegrationModule.class, module, "openperipheral.IntegrationModuleRegistry", "registerModule");
	}

	private static boolean register(Class<?> type, Object obj, String klazzName, String methodName) {
		try {
			Class<?> klazz = Class.forName(klazzName);
			if (klazz != null) {
				Method method = klazz.getMethod(methodName, new Class[] { type });
				method.invoke(null, obj);
				return true;
			}
		} catch (Exception e) {}
		return false;
	}
}
