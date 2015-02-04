package openperipheral.api.adapter;

/**
 *
 * Used to register adapters for classes. They will be later used to collect methods for wrapped objects.
 *
 * @see IObjectAdapterRegistry
 * @see IPeripheralAdapterRegistry
 *
 */
public interface IAdapterRegistry<T extends IAdapter> {
	/**
	 * Register external adapter
	 */
	public boolean register(T adapter);

	/**
	 * Precalculate internal adapter (i.e. scan class for declared methods). May be used for error checking
	 */
	public void registerInline(Class<?> cls);
}
