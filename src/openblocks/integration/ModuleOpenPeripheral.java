package openblocks.integration;

import openperipheral.api.OpenPeripheralAPI;

public class ModuleOpenPeripheral {

	public static void registerAdapters() {
		OpenPeripheralAPI.register(new AdapterVillageHighlighter());
		OpenPeripheralAPI.register(new AdapterDonationStation());
		OpenPeripheralAPI.register(new AdapterCannon());
		OpenPeripheralAPI.register(new AdapterProjector());
	}
}
