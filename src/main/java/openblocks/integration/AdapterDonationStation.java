package openblocks.integration;

import openblocks.common.tileentity.TileEntityDonationStation;
import openperipheral.api.adapter.IPeripheralAdapter;
import openperipheral.api.adapter.method.IMultiReturn;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.helpers.MultiReturn;

public class AdapterDonationStation implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityDonationStation.class;
	}

	@Override
	public String getSourceId() {
		return "openblocks_donation";
	}

	@ScriptCallable(returnTypes = { ReturnType.STRING, ReturnType.STRING }, description = "Find the mod name and mod authors")
	public IMultiReturn getItemAuthor(final TileEntityDonationStation station) {
		if (station.getInventory().getStackInSlot(0) == null) return null;
		return MultiReturn.wrap(station.getModName(), station.getModAuthors());
	}
}
