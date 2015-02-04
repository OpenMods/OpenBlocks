package openperipheral.api.adapter;

import openperipheral.api.adapter.method.ScriptCallable;

/**
 * Base for peripheral and object adapters
 */
public abstract interface IAdapter {

	/**
	 * Adapter identifier, used for documentation purposes ({@code .listSources()} on Lua objects).
	 */
	public String getSourceId();

	/**
	 * Return target class for this adapter.
	 * It will be used to determine if methods marked with {@link ScriptCallable} in implementing class are applicable to instances of returned class.
	 */
	public Class<?> getTargetClass();
}
