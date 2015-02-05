package openperipheral.api.adapter;

import openperipheral.api.adapter.method.ScriptCallable;

/**
 * The external peripheral adapter allows you to define methods that can be run against the target tile entity returned in {@link #getTargetClass()}.
 *
 * To create methods, simpily add methods to your IPeripheralAdapter and annotate them with {@link ScriptCallable}.
 *
 * @see IPeripheralAdapterRegistry#register(IPeripheralAdapter)
 *
 * @author mikeef
 *
 */

public interface IPeripheralAdapter extends IAdapter {}
