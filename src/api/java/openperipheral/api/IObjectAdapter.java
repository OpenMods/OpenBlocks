package openperipheral.api;

/**
 *
 * Interface for registering external adapters for Lua object wrappers.
 *
 * @see OpenPeripheralAPI#register(IObjectAdapter)
 * @see IPeripheralAdapter
 */
public interface IObjectAdapter extends IAdapterBase {

	/**
	 * Return target class for this adapter. It will be used to determine, if other methods in implementing class are applicable to object.
	 */
	public Class<?> getTargetClass();
}
