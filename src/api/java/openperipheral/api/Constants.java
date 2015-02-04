package openperipheral.api;

import openperipheral.api.adapter.method.Env;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.converter.IConverter;

/**
 * Various constants used in API methods.
 */
public class Constants {
	public static final String ARCH_COMPUTER_CRAFT = "ComputerCraft";
	public static final String ARCH_OPEN_COMPUTERS = "OpenComputers";

	/**
	 * Environment variable (see {@link Env} for calling machine context.
	 * Type depends on architecture:
	 * <ul>
	 * <li>ComputerCraft - {@link dan200.computercraft.api.lua.ILuaContext}</li>
	 * <li>OpenComputers - {@link li.cil.oc.api.machine.Context}</li>
	 * </ul>
	 */
	public static final String ARG_CONTEXT = "context";

	/**
	 * Environment variable (see {@link Env} for computer access.
	 * Available only for ComputerCraft, needs type {@link dan200.computercraft.api.peripheral.IComputerAccess}.
	 */
	public static final String ARG_COMPUTER = "computer";

	/**
	 * Environment variable (see {@link Env} for type converter ({@link IConverter}).
	 * Returned instance will be valid for current architecture
	 */
	public static final String ARG_CONVERTER = "converter";

	/**
	 * Environment variable (see {@link Env} for architecture-independent access to machine ({@link IArchitectureAccess}).
	 * Returned instance will be valid for current architecture.
	 */
	public static final String ARG_ACCESS = "access";

	/**
	 * Environment variable (see {@link Env} for target, used internally.
	 */
	public static final String ARG_TARGET = "target";
}
