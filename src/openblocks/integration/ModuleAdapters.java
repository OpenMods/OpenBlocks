package openblocks.integration;

import openmods.integration.ApiIntegration;
import openperipheral.api.OpenPeripheralAPI;

public class ModuleAdapters extends ApiIntegration {

	public ModuleAdapters() {
		super("openperipheral.api.OpenPeripheralAPI");
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
