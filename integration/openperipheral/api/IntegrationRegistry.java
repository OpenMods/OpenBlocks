package openperipheral.api;

import java.lang.reflect.Method;

public class IntegrationRegistry {

	@Deprecated
	public boolean registerAdapter(IPeripheralAdapter adapter) {
		return register(IPeripheralAdapter.class, adapter, "openperipheral.core.AdapterManager", "addPeripheralAdapter");
	}

	@Deprecated
	public boolean registerTypeConverter(ITypeConverter converter) {
		return register(ITypeConverter.class, converter, "openperipheral.core.TypeConversionRegistry", "registerTypeConverter");
	}

	@Deprecated
	private boolean register(Class<?> type, Object obj, String klazzName, String methodName) {
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
	
	public static boolean registerPeripheralAdapter(IPeripheralAdapter adapter) {
		return registerClass(IPeripheralAdapter.class, adapter, "openperipheral.core.AdapterManager", "addPeripheralAdapter");
	}

	public static boolean registerConverter(ITypeConverter converter) {
		return registerClass(ITypeConverter.class, converter, "openperipheral.core.TypeConversionRegistry", "registerTypeConverter");
	}

	private static boolean registerClass(Class<?> type, Object obj, String klazzName, String methodName) {
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
