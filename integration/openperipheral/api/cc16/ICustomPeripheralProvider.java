package openperipheral.api.cc16;

import dan200.computercraft.api.peripheral.IPeripheral;

public interface ICustomPeripheralProvider {
	public IPeripheral createPeripheral(int side);
}
