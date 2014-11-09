package openperipheral.api;

import java.lang.reflect.Method;

import com.google.common.base.Throwables;

public class ApiAccess {
	private static Method providerMethod;

	@SuppressWarnings("unchecked")
	public static <T extends IApiInterface> T getApi(Class<T> cls) {
		try {
			if (providerMethod == null) {
				Class<?> providerCls = Class.forName("openperipheral.ApiProvider");
				providerMethod = providerCls.getMethod("provideImplementation", Class.class);
			}

			return (T)providerMethod.invoke(null, cls);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
}
