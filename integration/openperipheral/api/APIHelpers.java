package openperipheral.api;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;

public class APIHelpers {

	public static final Logger logger;

	static {
		logger = Logger.getLogger("OpenPeripheral API");
		logger.setParent(FMLLog.getLogger());
	}

	public static <A> boolean callWithoutReturn(String klazzName, String methodName, Class<? extends A> argType, A argValue) {
		try {
			Method method = OpenPeripheralAPI.getMethod(klazzName, methodName, argType);
			method.invoke(null, argValue);
			return true;
		} catch (Throwable t) {
			logger.log(Level.WARNING, String.format("Exception while calling method '%s'", methodName), t);
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public static <A, T> T callWithReturn(String klazzName, String methodName, Class<? extends A> argType, A argValue, Class<? extends T> returnType) {
		T result;
		try {
			Method method = OpenPeripheralAPI.getMethod(klazzName, methodName, argType);
			result = (T)method.invoke(null, argValue);
		} catch (Throwable t) {
			logger.log(Level.WARNING, String.format("Exception while calling method '%s'", methodName), t);
			return null;
		}

		if (result == null || returnType.isInstance(result)) {
			return result;
		} else {
			logger.log(Level.WARNING, String.format("Method '%s' return type '%s' cannot be cast to '%s'", methodName, result.getClass(), returnType));
			return null;
		}
	}

}
