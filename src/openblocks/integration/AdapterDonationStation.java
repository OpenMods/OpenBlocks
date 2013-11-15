package openblocks.integration;

import openblocks.common.tileentity.TileEntityDonationStation;
import openperipheral.api.IMultiReturn;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterDonationStation implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityDonationStation.class;
	}

	@SuppressWarnings({ "unused" })
	@LuaMethod(onTick = true, returnType = LuaType.STRING, description = "Find the mod name and mod authors")
	public IMultiReturn getItemAuthor(IComputerAccess computer, final TileEntityDonationStation station) {
		if (station.getStackInSlot(0) != null) { return new IMultiReturn() {
			@Override
			public Object[] getObjects() {
				return new Object[] { station.getModName().getValue(), station.getAuthors().getValue() };
			}
		}; }
		return null;
	}
}
