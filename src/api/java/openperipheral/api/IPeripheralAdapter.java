package openperipheral.api;

/**
 * The external peripheral adapter allows you to define methods that can be run against the target tile entity returned in {@link #getTargetClass()}.
 *
 * To create methods, simpily add methods to your IPeripheralAdapter and annotate them with {@link LuaMethod} or {@link LuaCallable}.
 *
 * @see OpenPeripheralAPI#register(IPeripheralAdapter)
 *
 * @author mikeef
 *
 */

public interface IPeripheralAdapter extends IAdapter {}
