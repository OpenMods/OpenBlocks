package openperipheral.api;

/**
 * Base for peripheral and object adapters
 */
public abstract interface IAdapterBase {

	/**
	 * Adapter identifier, used for documentation purposes ({@code .listSources()} on Lua objects)
	 */
	public String getSourceId();
}
