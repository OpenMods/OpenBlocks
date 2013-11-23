package openblocks.integration;

import openperipheral.api.IntegrationRegistry;

public class ModuleOpenPeripheral {

	public static void registerAdapters() {
		IntegrationRegistry.registerPeripheralAdapter(new AdapterVillageHighlighter());
		IntegrationRegistry.registerPeripheralAdapter(new AdapterDonationStation());
		IntegrationRegistry.registerPeripheralAdapter(new AdapterCannon());
		IntegrationRegistry.registerPeripheralAdapter(new AdapterProjector());
	}
}
