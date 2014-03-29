package openperipheral.api.cc15x;

import dan200.computer.api.IComputerAccess;

/**
 * Tile Entities marked with this annotation will be informed when computer is attached
 * This version is used for interaction with CC before 1.6
 */
public interface IAttachable {
	public void addComputer(IComputerAccess computer);

	public void removeComputer(IComputerAccess computer);
}
