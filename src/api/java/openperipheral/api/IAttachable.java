package openperipheral.api;

import dan200.computercraft.api.peripheral.IComputerAccess;

/**
 * Tile Entities marked with this annotation will be informed when computer is attached.
 * This version is used for interaction with CC 1.6 and later
 */
public interface IAttachable {
	public void addComputer(IComputerAccess computer);

	public void removeComputer(IComputerAccess computer);
}
