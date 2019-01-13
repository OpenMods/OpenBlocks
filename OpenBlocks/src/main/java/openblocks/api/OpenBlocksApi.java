package openblocks.api;

public class OpenBlocksApi {
	public interface ApiProvider {
		<T extends IApiInterface> T getApi(Class<T> cls);

		<T extends IApiInterface> boolean isApiPresent(Class<T> cls);
	}

	private OpenBlocksApi() {}

	private static ApiProvider provider;

	// OpenBlocks will use this method to provide actual implementation
	public static void init(ApiProvider provider) {
		if (OpenBlocksApi.provider != null) throw new IllegalStateException("API already initialized");
		OpenBlocksApi.provider = provider;
	}

	/**
	 * @deprecated Use {@link ApiHolder}.
	 */
	@Deprecated
	public static <T extends IApiInterface> T getApi(Class<T> cls) {
		if (provider == null) throw new IllegalStateException("API not initialized");
		return provider.getApi(cls);
	}

	public static <T extends IApiInterface> boolean isApiPresent(Class<T> cls) {
		if (provider == null) throw new IllegalStateException("API not initialized");
		return provider.isApiPresent(cls);
	}
}
