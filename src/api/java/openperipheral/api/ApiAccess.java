package openperipheral.api;

import openperipheral.api.adapter.IObjectAdapterRegistry;
import openperipheral.api.adapter.IPeripheralAdapterRegistry;
import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IConverterManager;
import openperipheral.api.meta.IEntityMetaBuilder;
import openperipheral.api.meta.IItemStackMetaBuilder;
import openperipheral.api.peripheral.IPeripheralBlacklist;

/**
 * This class is used to get implementation of API interfaces (subclasses of {@link IApiInterface}.
 *
 * Most commonly used interfaces:
 * <ul>
 * <li>{@link IComputerCraftObjectsFactory} - for creating ComputerCraft wrappers for normal Java objects</li>
 * <li>{@link IPeripheralAdapterRegistry} - for registering peripheral adapters</li>
 * <li>{@link IObjectAdapterRegistry} - for registering object adapters</li>
 * <li>{@link IEntityMetaBuilder} - for registering metadata providers and getting metadata for in-game entitites</li>
 * <li>{@link IItemStackMetaBuilder} - for registering metadata providers and getting metadata for in-game items</li>
 * <li>{@link IConverter} - for registering type converters and converting values from and to Lua. <strong>Object returned for this interface should not be used for conversion</strong></li>
 * <li>{@link IConverterManager} - for getting architecture-specific type converters</li>
 * <li>{@link IPeripheralBlacklist} - for checking if class is blacklisted (i.e. will not generate peripheral)</li>
 * </ul>
 */
public class ApiAccess {
	public static final String API_VERSION = "3.0";

	public interface ApiProvider {
		public <T extends IApiInterface> T getApi(Class<T> cls);

		public <T extends IApiInterface> boolean isApiPresent(Class<T> cls);
	}

	private ApiAccess() {}

	private static ApiProvider provider;

	// OpenPeripheralCore will use this method to provide actual implementation
	public static void init(ApiProvider provider) {
		if (ApiAccess.provider != null) throw new IllegalStateException("API already initialized");
		ApiAccess.provider = provider;
	}

	public static <T extends IApiInterface> T getApi(Class<T> cls) {
		if (provider == null) throw new IllegalStateException("API not initialized");
		return provider.getApi(cls);
	}

	public static <T extends IApiInterface> boolean isApiPresent(Class<T> cls) {
		if (provider == null) throw new IllegalStateException("API not initialized");
		return provider.isApiPresent(cls);
	}
}
