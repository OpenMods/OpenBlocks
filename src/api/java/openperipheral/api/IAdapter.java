package openperipheral.api;

/**
 * Base for peripheral and object adapters
 */
public abstract interface IAdapter {

	/**
	 * Adapter identifier, used for documentation purposes ({@code .listSources()} on Lua objects)
	 */
	public String getSourceId();

	/**
	 * Return target class for this adapter. It will be used to determine, if other methods in implementing class are applicable to object.
	 */
	public Class<?> getTargetClass();
}
