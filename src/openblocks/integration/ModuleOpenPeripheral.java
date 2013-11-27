package openblocks.integration;

import openperipheral.api.IntegrationRegistry;

public class ModuleOpenPeripheral {

	private static final IntegrationRegistry reg = new IntegrationRegistry();
	
	@SuppressWarnings("deprecation")
	public static void registerAdapters() {
		reg.registerAdapter(new AdapterVillageHighlighter());
		reg.registerAdapter(new AdapterDonationStation());
		reg.registerAdapter(new AdapterCannon());
		reg.registerAdapter(new AdapterProjector());
	}
}
