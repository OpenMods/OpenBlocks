package openperipheral.api.adapter;

import openperipheral.api.IApiInterface;

/**
 * API interface for registering adapters for peripherals (i.e. objects existing in world)
 *
 */
public interface IPeripheralAdapterRegistry extends IApiInterface, IAdapterRegistry<IPeripheralAdapter> {}
