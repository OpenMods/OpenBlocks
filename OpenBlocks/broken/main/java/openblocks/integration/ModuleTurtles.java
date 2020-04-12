package openblocks.integration;

import static openmods.conditions.Conditions.all;
import static openmods.integration.IntegrationConditions.classExists;
import static openmods.integration.IntegrationConditions.modLoaded;

import dan200.computercraft.api.ComputerCraftAPI;
import openblocks.OpenBlocks.Items;
import openmods.Mods;
import openmods.integration.IntegrationModule;

public class ModuleTurtles extends IntegrationModule {

	public ModuleTurtles() {
		super(all(
				modLoaded(Mods.OPENPERIPHERALCORE),
				modLoaded(Mods.COMPUTERCRAFT),
				classExists("dan200.computercraft.api.turtle.ITurtleUpgrade")));
	}

	@Override
	public String name() {
		return "OpenBlocks turtles";
	}

	@Override
	public void load() {
		LoadHack.load();
	}

	private static class LoadHack {
		private static void load() {
			if (Items.miracleMagnet != null) ComputerCraftAPI.registerTurtleUpgrade(new MagnetTurtleUpgrade());
		}
	}

}
