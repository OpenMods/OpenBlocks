package openblocks.integration.cc15;

import static openmods.conditions.Conditions.all;
import static openmods.integration.Conditions.classExists;
import static openmods.integration.Conditions.modLoaded;
import net.minecraftforge.common.MinecraftForge;
import openmods.Mods;
import openmods.OpenMods;
import openmods.integration.IntegrationModule;

public class ModuleTurtlesCC15X extends IntegrationModule {

	public ModuleTurtlesCC15X() {
		super(all(
				modLoaded(Mods.OPENPERIPHERALCORE),
				modLoaded(Mods.COMPUTERCRAFT_TURTLE),
				classExists("dan200.turtle.api.ITurtleUpgrade")));
	}

	@Override
	public String name() {
		return "OpenBlocks turtles for CC1.5X";
	}

	@Override
	public void load() {
		LoadHack.load();
	}

	private static class LoadHack {
		public static void load() {
			final MagnetTurtleUpgrade magnetUpgrade = new MagnetTurtleUpgrade();
			TurtleAPI.registerUpgrade(magnetUpgrade);
			if (!OpenMods.proxy.isServerOnly()) MinecraftForge.EVENT_BUS.register(magnetUpgrade);
		}
	}

}
