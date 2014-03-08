package openperipheral.api;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import cpw.mods.fml.common.FMLLog;
import dan200.computer.api.IHostedPeripheral;

public class OpenPeripheralAPI {

	public static final Logger logger;

	static {
		logger = Logger.getLogger("OpenPeripheral API");
		logger.setParent(FMLLog.getLogger());
	}

	public static boolean register(IPeripheralAdapter adapter) {
		return callWithoutReturn("openperipheral.adapter.AdapterManager", "addPeripheralAdapter", IPeripheralAdapter.class, adapter);
	}

	public static boolean register(IObjectAdapter adapter) {
		return callWithoutReturn("openperipheral.adapter.AdapterManager", "addObjectAdapter", IPeripheralAdapter.class, adapter);
	}

	public static boolean register(ITypeConverter converter) {
		return callWithoutReturn("openperipheral.TypeConversionRegistry", "registerTypeConverter", ITypeConverter.class, converter);
	}

	public static boolean register(IIntegrationModule module) {
		return callWithoutReturn("openperipheral.IntegrationModuleRegistry", "registerModule", IIntegrationModule.class, module);
	}

	public static boolean createAdapter(Class<? extends TileEntity> cls) {
		return callWithoutReturn("openperipheral.adapter.AdapterManager", "addInlinePeripheralAdapter", Class.class, cls);
	}

	public static IHostedPeripheral createHostedPeripheral(Object target) {
		return callWithReturn("openperipheral.adapter.AdapterManager", "createHostedPeripheral", Object.class, target, IHostedPeripheral.class);
	}

	private static Method getMethod(String klazzName, String methodName, Class<?> argType) throws Exception {
		Class<?> klazz = Class.forName(klazzName);
		return klazz.getMethod(methodName, new Class[] { argType });
	}

	private static <A> boolean callWithoutReturn(String klazzName, String methodName, Class<? extends A> argType, A argValue) {
		try {
			Method method = getMethod(klazzName, methodName, argType);
			method.invoke(null, argValue);
			return true;
		} catch (Throwable t) {
			logger.log(Level.WARNING, String.format("Exception while calling method '%s'", methodName), t);
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private static <A, T> T callWithReturn(String klazzName, String methodName, Class<? extends A> argType, A argValue, Class<? extends T> returnType) {
		T result;
		try {
			Method method = getMethod(klazzName, methodName, argType);
			result = (T)method.invoke(null, argValue);
		} catch (Throwable t) {
			logger.log(Level.WARNING, String.format("Exception while calling method '%s'", methodName), t);
			return null;
		}

		if (result == null || returnType.isInstance(result)) {
			return result;
		} else {
			Log.log(Level.WARNING, "Method '%s' return type '%s' cannot be cast to '%s'", methodName, result.getClass(), returnType);
			return null;
		}
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
