package openperipheral.api;

import dan200.computercraft.api.peripheral.IPeripheral;

/**
 *
 * Class used for TileEntities that wish to provide its own peripheral implementation
 */
public interface ICustomPeripheralProvider {
	public IPeripheral createPeripheral(int side);
}
