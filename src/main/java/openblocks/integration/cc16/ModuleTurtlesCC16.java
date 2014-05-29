package openblocks.integration.cc16;

import static openmods.conditions.Conditions.all;
import static openmods.integration.Conditions.classExists;
import static openmods.integration.Conditions.modLoaded;
import net.minecraftforge.common.MinecraftForge;
import openmods.Mods;
import openmods.OpenMods;
import openmods.integration.IntegrationModule;

public class ModuleTurtlesCC16 extends IntegrationModule {

	public ModuleTurtlesCC16() {
		super(all(
				modLoaded(Mods.OPENPERIPHERALCORE),
				modLoaded(Mods.COMPUTERCRAFT),
				classExists("dan200.computercraft.api.turtle.ITurtleUpgrade")));
	}

	@Override
	public String name() {
		return "OpenBlocks turtles for CC1.6";
	}

	@Override
	public void load() {
		LoadHack.load();
	}

	private static class LoadHack {
		private static void load() {
			final MagnetTurtleUpgrade magnetUpgrade = new MagnetTurtleUpgrade();
			ComputerCraftAPI.registerTurtleUpgrade(magnetUpgrade);
			if (!OpenMods.proxy.isServerOnly()) MinecraftForge.EVENT_BUS.register(magnetUpgrade);
		}
	}

}
