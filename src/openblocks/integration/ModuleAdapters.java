package openblocks.integration;

import static openmods.integration.Conditions.modLoaded;
import openmods.Mods;
import openmods.integration.IntegrationModule;
import openperipheral.api.OpenPeripheralAPI;

public class ModuleAdapters extends IntegrationModule {

	public ModuleAdapters() {
		super(modLoaded(Mods.OPENPERIPHERALCORE));
	}

	@Override
	public String name() {
		return "OpenBlocks adapters for OpenPeripheral";
	}

	@Override
	public void load() {
		OpenPeripheralAPI.register(new AdapterVillageHighlighter());
		OpenPeripheralAPI.register(new AdapterDonationStation());
		OpenPeripheralAPI.register(new AdapterCannon());
		OpenPeripheralAPI.register(new AdapterProjector());
	}
}
