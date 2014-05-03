/**
 * Main purpose of OpenPeripheral is to automatically generate ComputerCraft peripherals for mods that don't do it for themselves.
 * It may also be used by mod creators who don't want to write their own addons using bare CC API.
 * 
 * <h3>Features</h3>
 * <ul>
 * <li>Automatical generation of ComputerCraft peripherals</li>
 * <li>Transparent and extensible conversion of argument and return values from and to Lua</li>
 * <li>Automatic documentation for peripherals with {@code listMethods} and {@code getAdvancedMethodsData}</li>
 * <li>Generated peripherals will contain every method applicable to TileEntity and implemented interfaces</li> 
 * </ul>
 * 
 * <h3>Usage</h3>
 * OpenPeripheral will automatically register peripherals in ComputerCraft API for every tile entity that has known methods (i.e. declared in adapters).
 * It won't register TE if it already implements IPeripheral - OpenPeripheralk only works for TEs without CC integration defined by author.
 * 
 * Peripherals can be also generated manually through {@link openperipheral.api.OpenPeripheralAPI#createHostedPeripheral(Object)}, for example when one is needed for turtle upgrade 
 * 
 * OpenPeripheral can be also used to wrap non-TE java objects as ILuaObjects. It can be done by using {@link openperipheral.api.OpenPeripheralAPI#createWrapper(Object)}
 * 
 * <h3>Adapters</h3>
 * Adapters are source of information about method that can be exposed to Lua user.
 * They usually contain Java methods (see {@link openperipheral.api.LuaCallable} and {@link openperipheral.api.LuaMethod} with metadata that will be used for documentation and validation.
 * Adapters declare target class, that is later used for determining, which methods will be used for peripheral  
 * Adapter can be defined in two places:
 * <ul>
 * <li>external adapters - declared as stand alone classes (implementing either {@link openperipheral.api.IPeripheralAdapter} or {@link openperipheral.api.IObjectAdapter}
 * <li>internal/inline adapters - declared directly in wrapped objects</li>
 * </ul>
 * 
 * 
 * <h3>Method arguments</h3>
 * Methods declared in adapters must declare arguments in following order:
 * <ol>
 * <li>Java arguments - have name that declares it's purpose (see below)</li>
 * <li>Converted Lua arguments - must be marked with {@link openperipheral.api.Arg} annotation</li>
 * </ol>
 * 
 * During call Java arguments are filled with values depending on it's names. Arguments can be names with either {@link openperipheral.api.Named} or {@link openperipheral.api.Prefixed} annotations.
 * Predefined names:
 * <ul>
 * <li>{@code target} - used in external adapter to mark argument that will be filled with target object (for peripheral adapters it will be TileEntity instance). Type must be supertype or interface of target class</li>
 * <li>{@code computer} - field of type IComputerAccess. Available only for peripheral adapters </li>
 * <li>{@code context} - field of type ILuaContext </li>
 * </ul>
 * If there are no annotations, default configuration is used:
 * <ul>
 * <li>External peripheral adapters: {@code target, computer}</li>
 * <li>Internal peripheral adapters: {@code computer}</li>
 * <li>External object adapters: {@code target, context}</li>
 * <li>Internal object adapters: {@code context}</li>
 * </ul>
 */

@API(apiVersion = "1.0", owner = "OpenPeripheralCore", provides = "OpenPeripheralApi")
package openperipheral.api;

import cpw.mods.fml.common.API;

