package openblocks.integration;

import static openmods.integration.Conditions.modLoaded;
import openmods.Mods;
import openmods.integration.IntegrationModule;
import openperipheral.api.ApiAccess;
import openperipheral.api.adapter.IPeripheralAdapterRegistry;

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
			final IPeripheralAdapterRegistry registry = ApiAccess.getApi(IPeripheralAdapterRegistry.class);
			registry.register(new AdapterVillageHighlighter());
			registry.register(new AdapterDonationStation());
			registry.register(new AdapterCannon());
			registry.register(new AdapterProjector());
		}
	}
}
