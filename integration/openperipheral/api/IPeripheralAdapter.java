package openperipheral.api;

/**
 * The peripheral adapter allows you to define methods that can be run against
 * the target tile entity returned in getTargetClass().
 * 
 * To create methods, simpily add methods to your IPeripheralAdapter and
 * annotate
 * them with @LuaMethod
 * The first parameter of your custom methods must accept an IComputerAccess
 * The second parameter of your custom method must accept the type defined in
 * your getTargetClass()
 * 
 * For example:
 * 
 * @LuaMethod
 *            public boolean setLocation(IComputerAccess computer, ITeleporter
 *            teleporter, int x, int y, int z)
 * 
 *            To register this adapter, call
 *            IntegrationRegistry.registerAdapter(new MyAdapter())
 * 
 * @author mikeef
 * 
 */

public interface IPeripheralAdapter extends IAdapterBase {
	public Class<?> getTargetClass();
}
