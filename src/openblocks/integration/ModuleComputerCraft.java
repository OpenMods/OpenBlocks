package openblocks.integration;

import net.minecraftforge.common.MinecraftForge;
import openmods.OpenMods;
import dan200.turtle.api.TurtleAPI;

public class ModuleComputerCraft {

	public static MagnetTurtleUpgrade magnetUpgrade;

	public static void registerAddons() {
		magnetUpgrade = new MagnetTurtleUpgrade();
		TurtleAPI.registerUpgrade(magnetUpgrade);
		if (!OpenMods.proxy.isServerOnly()) MinecraftForge.EVENT_BUS.register(magnetUpgrade);
	}
}
