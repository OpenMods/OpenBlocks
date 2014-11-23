package openperipheral.api;

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
 * <li>{@link IPeripheralBlacklist} - for checking if class is blacklisted (i.e. will not generate peripheral)</li>
 * </ul>
 */
public class ApiAccess {
	public static final String API_VERSION = "2.2";

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
