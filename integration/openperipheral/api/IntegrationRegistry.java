package openperipheral.api;

import java.lang.reflect.Method;

public class IntegrationRegistry {

	/**
	 * @deprecated Use static version {@link #register(IPeripheralAdapter)}
	 */
	@Deprecated
	public boolean registerAdapter(IPeripheralAdapter adapter) {
		return register(adapter);
	}

	/**
	 * @deprecated Use static version {@link #register(ITypeConverter)}
	 */
	@Deprecated
	public boolean registerTypeConverter(ITypeConverter converter) {
		return register(converter);
	}

	public static boolean register(IPeripheralAdapter adapter) {
		return register(IPeripheralAdapter.class, adapter, "openperipheral.core.AdapterManager", "addPeripheralAdapter");
	}

	public static boolean register(ITypeConverter converter) {
		return register(ITypeConverter.class, converter, "openperipheral.core.TypeConversionRegistry", "registerTypeConverter");
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
