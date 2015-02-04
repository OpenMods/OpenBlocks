package openperipheral.api.architecture.cc;

import dan200.computercraft.api.peripheral.IPeripheral;

/**
 *
 * Class used for TileEntities that wish to provide its own peripheral implementation. No extra methods will be added.
 * This class is for ComputerCraft only.
 */
public interface ICustomPeripheralProvider {
	public IPeripheral createPeripheral(int side);
}
