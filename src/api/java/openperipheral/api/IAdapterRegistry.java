package openperipheral.api;

/**
 * API interface for registering adapters
 *
 */
public interface IAdapterRegistry extends IApiInterface {
	public boolean register(IPeripheralAdapter adapter);

	public boolean register(IObjectAdapter adapter);

	public void registerInline(Class<?> cls);
}
