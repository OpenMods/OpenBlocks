package openperipheral.api;

import dan200.computercraft.api.peripheral.IPeripheral;

public interface ICustomPeripheralProvider {
	public IPeripheral createPeripheral(int side);
}
