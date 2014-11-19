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
 * It won't register TE if it already implements IPeripheral - OpenPeripheral only works for TEs without CC integration defined by author.
 *
 * OpenPeripheral can be also used to wrap objects to ComputerCraft structures (like {@link dan200.computercraft.api.peripheral.IPeripheral} and {@link dan200.computercraft.api.lua.ILuaObject}). It can be done by using {@link openperipheral.api.IAdapterFactory}
 *
 * <h3>Adapters</h3>
 * Adapters are source of information about method that can be exposed to Lua user.
 * They usually contain Java methods (see {@link openperipheral.api.LuaCallable} with metadata that will be used for documentation and validation.
 * Adapters declare target class, that is later used for determining, which methods will be used for peripheral
 * Adapter can be defined in two places:
 * <ul>
 * <li>external adapters - declared as stand alone classes (implementing either {@link openperipheral.api.IPeripheralAdapter} or {@link openperipheral.api.IObjectAdapter}
 * <li>internal/inline adapters - declared directly in wrapped objects</li>
 * </ul>
 *
 * <h3>Blacklisting tile entities</h3>
 * Due to inner working of ComputerCraft we can check if there are any other peripheral providers.
 * If mod author has chosen to provider integration with providers, OpenPeripheral implementation may sometimes hide actual, non-generated integration.
 * In this cases there are few ways to prevent our handler from creating adapter for TileEntity:
 * <ul>
 * <li>{@link openperipheral.api.Ignore} annotation on TileEntity class</li>
 * <li>Any field called {@code OPENPERIPHERAL_IGNORE} in TileEntity class</li>
 * <li>IMC message with id {@code ignoreTileEntity} and full class name as value</li>
 * </ul>
 *
 * <h3>Method arguments</h3>
 * Methods declared in adapters must declare arguments in following order:
 * <ol>
 * <li>Java arguments - have name that declares it's purpose (see below)</li>
 * <li>Converted Lua arguments - must be marked with {@link openperipheral.api.Arg} annotation</li>
 * </ol>
 *
 * During call Java arguments are filled with values depending on it's names. Arguments can be names with either {@link openperipheral.api.Env} or {@link openperipheral.api.Prefixed} annotations.
 * Predefined names:
 * <ul>
 * <li>{@code target} - used in external adapter to mark argument that will be filled with target object (for peripheral adapters it will be TileEntity instance). Type must be supertype or interface of target class</li>
 * <li>{@code computer} - field of type IComputerAccess. Available only for peripheral adapters </li>
 * <li>{@code context} - field of type ILuaContext </li>
 * </ul>
 */

@API(apiVersion = openperipheral.api.ApiAccess.API_VERSION, owner = "OpenPeripheralCore", provides = "OpenPeripheralApi")
package openperipheral.api;

import cpw.mods.fml.common.API;

