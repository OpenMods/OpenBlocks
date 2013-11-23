package openperipheral.api;

import dan200.computer.api.IComputerAccess;

public interface IAttachable {
	public void addComputer(IComputerAccess computer);

	public void removeComputer(IComputerAccess computer);
}
