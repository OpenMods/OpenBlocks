package openperipheral.api.adapter;

import openperipheral.api.IApiInterface;

/**
 * API interface for registering adapters for Lua visible objects
 *
 */
public interface IObjectAdapterRegistry extends IApiInterface, IAdapterRegistry<IObjectAdapter> {}
