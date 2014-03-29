package openperipheral.api;

import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;

public class OpenPeripheralAPI {

	public static boolean register(IPeripheralAdapter adapter) {
		return APIHelpers.callWithoutReturn("openperipheral.adapter.AdapterManager", "addPeripheralAdapter", IPeripheralAdapter.class, adapter);
	}

	public static boolean register(IObjectAdapter adapter) {
		return APIHelpers.callWithoutReturn("openperipheral.adapter.AdapterManager", "addObjectAdapter", IPeripheralAdapter.class, adapter);
	}

	public static boolean register(ITypeConverter converter) {
		return APIHelpers.callWithoutReturn("openperipheral.TypeConversionRegistry", "registerTypeConverter", ITypeConverter.class, converter);
	}

	public static boolean register(IIntegrationModule module) {
		return APIHelpers.callWithoutReturn("openperipheral.IntegrationModuleRegistry", "registerModule", IIntegrationModule.class, module);
	}

	public static boolean createAdapter(Class<? extends TileEntity> cls) {
		return APIHelpers.callWithoutReturn("openperipheral.adapter.AdapterManager", "addInlinePeripheralAdapter", Class.class, cls);
	}

	static Method getMethod(String klazzName, String methodName, Class<?> argType) throws Exception {
		Class<?> klazz = Class.forName(klazzName);
		return klazz.getMethod(methodName, new Class[] { argType });
	}

	public static IMultiReturn wrap(final Object... args) {
		return new IMultiReturn() {
			@Override
			public Object[] getObjects() {
				return args;
			}
		};
	}
}
