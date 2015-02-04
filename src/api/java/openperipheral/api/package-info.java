/**
 * Main purpose of OpenPeripheral is to automatically generate objects that can be handled by scriptable environments like ComputerCraft or OpenComputers.
 * It may also be used by mod creators who don't want to write their own addons using bare APIs of mentioned mods.
 *
 * <h3>Features</h3>
 * <ul>
 * <li>Automatical generation of peripherals/environments</li>
 * <li>Transparent and extensible conversion of argument and return values between Java and script enviroment</li>
 * <li>Automatic documentation for peripherals with {@code listMethods} and {@code getAdvancedMethodsData}</li>
 * <li>Generated peripherals will contain every method applicable to TileEntity and implemented interfaces</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * OpenPeripheral will automatically provide peripherals for every tile entity that has methods known to mod (i.e. declared in adapters).
 * When providing peripheral for ComputerCraft, it won't operate on TE if it already implements IPeripheral - OpenPeripheral only works for TEs without CC integration defined by author.
 * When providing peripheral for OpenComputers, it will merge own methods will low priority, so it shouldn't interfere with existing drivers
 *
 * OpenPeripheral can be also used to wrap objects to ComputerCraft structures (like {@link dan200.computercraft.api.peripheral.IPeripheral} and {@link dan200.computercraft.api.lua.ILuaObject}). It can be done by using {@link openperipheral.api.architecture.cc.IComputerCraftObjectsFactory}
 *
 * <h3>Adapters</h3>
 * Adapters are source of methods that will be be exposed to script enviroment.
 * They contain Java methods (see {@link openperipheral.api.adapter.method.ScriptCallable} with metadata that will be used for documentation and validation.
 * Adapters declare target class, that is later used for determining, which methods will be used for peripheral.
 * Adapter can be defined in two places:
 * <ul>
 * <li>external adapters - declared as stand alone classes (implementing either {@link openperipheral.api.adapter.IPeripheralAdapter} or {@link openperipheral.api.adapter.IObjectAdapter}
 * <li>internal/inline adapters - declared directly in objects</li>
 * </ul>
 *
 * If single class has more than one adapter available, they will be merged/
 *
 * <h3>Blacklisting tile entities</h3>
 * Due to inner working of ComputerCraft we can't check if there are any other peripheral providers that apply to tile entity.
 * If mod author has chosen to provider integration with providers, OpenPeripheral implementation may sometimes hide actual, non-generated integration.
 * In this cases there are few ways to prevent our handler from creating adapter for TileEntity:
 * <ul>
 * <li>{@link openperipheral.api.peripheral.Ignore} annotation on TileEntity class</li>
 * <li>Any field called {@code OPENPERIPHERAL_IGNORE} in TileEntity class</li>
 * <li>IMC message with id {@code ignoreTileEntity} and full class name as value</li>
 * <li>Explicit registration via {@link openperipheral.api.peripheral.IPeripheralBlacklist#addToBlacklist(Class)}
 * </ul>
 *
 * <h3>Method arguments</h3>
 * Methods declared in adapters must declare arguments in following order:
 * <ol>
 * <li>Target argument - present only for external adapters
 * <li>Environment arguments, exposing interfaces available to call by scripting environment (marked with {@link openperipheral.api.adapter.method.Env} annotation)</li>
 * <li>Convertable script arguments, visible to script and filled during call (marked with {@link openperipheral.api.adapter.method.Arg} annotation)</li>
 * </ol>
 *
 * Some of environment variables are defined in {@link openperipheral.api.Constants}
 */

@API(apiVersion = openperipheral.api.ApiAccess.API_VERSION, owner = "OpenPeripheralCore", provides = "OpenPeripheralApi")
package openperipheral.api;

import cpw.mods.fml.common.API;

