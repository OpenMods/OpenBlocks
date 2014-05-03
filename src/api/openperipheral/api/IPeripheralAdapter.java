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

public interface IPeripheralAdapter extends IAdapterBase {

	/**
	 * Return target class for this adapter. It will be used to determine, if other methods in implementing class are applicable to object.
	 * Should be either interface or TileEntity subclass.
	 */
	public Class<?> getTargetClass();
}
