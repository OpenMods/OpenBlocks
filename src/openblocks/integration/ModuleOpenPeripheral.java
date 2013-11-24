package openblocks.integration;

import openperipheral.api.IntegrationRegistry;

public class ModuleOpenPeripheral {

	public static void registerAdapters() {
		IntegrationRegistry.register(new AdapterVillageHighlighter());
		IntegrationRegistry.register(new AdapterDonationStation());
		IntegrationRegistry.register(new AdapterCannon());
		IntegrationRegistry.register(new AdapterProjector());
	}
}
