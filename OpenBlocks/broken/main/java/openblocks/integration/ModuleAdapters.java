package openblocks.integration;

import static openmods.integration.IntegrationConditions.modLoaded;

import openmods.Mods;
import openmods.integration.IntegrationModule;
import openperipheral.api.ApiHolder;
import openperipheral.api.adapter.IPeripheralAdapterRegistry;

public class ModuleAdapters extends IntegrationModule {

	@ApiHolder
	private static IPeripheralAdapterRegistry adapterRegistry;

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
			adapterRegistry.register(new AdapterVillageHighlighter());
			adapterRegistry.register(new AdapterDonationStation());
			adapterRegistry.register(new AdapterCannon());
			adapterRegistry.register(new AdapterProjector());
		}
	}
}
