package openblocks.integration;

import static openmods.integration.Conditions.modLoaded;
import openmods.Mods;
import openmods.integration.IntegrationModule;
import openperipheral.api.ApiAccess;
import openperipheral.api.IAdapterRegistry;

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
		LoadHack.load();
	}

	private static class LoadHack {
		private static void load() {
			final IAdapterRegistry registry = ApiAccess.getApi(IAdapterRegistry.class);
			registry.register(new AdapterVillageHighlighter());
			registry.register(new AdapterDonationStation());
			registry.register(new AdapterCannon());
			registry.register(new AdapterProjector());
		}
	}
}
