package openblocks.integration;

import openperipheral.api.IntegrationRegistry;

public class ModuleOpenPeripheral {

	public static void registerAdapters() {
		IntegrationRegistry registry = new IntegrationRegistry();
		registry.registerAdapter(new AdapterVillageHighlighter());
		registry.registerAdapter(new AdapterDonationStation());
		registry.registerAdapter(new AdapterCannon());
		registry.registerAdapter(new AdapterProjector());
	}
}
