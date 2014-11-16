package openperipheral.api;

import java.lang.reflect.Method;

import com.google.common.base.Throwables;

/**
 * This class is used to get implementation of API interfaces (subclasses of {@link IApiInterface}.
 *
 * Most commonly used interfaces:
 * <ul>
 * <li>{@link IAdapterFactory} - for creating ComputerCraft adapters for normal Java objects</li>
 * <li>{@link IAdapterRegistry} - for registering adapter classes</li>
 * <li>{@link IEntityMetaBuilder} - for registering metadata providers and getting metadata for in-game entitites</li>
 * <li>{@link IItemStackMetaBuilder} - for registering metadata providers and getting metadata for in-game items</li>
 * <li>{@link ITypeConvertersRegistry} - for registering type converters and converting values from and to Lua</li>
 * </ul>
 */
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
