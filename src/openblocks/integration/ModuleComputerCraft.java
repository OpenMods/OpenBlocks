package openblocks.integration;

import net.minecraftforge.common.MinecraftForge;
import openblocks.OpenBlocks;
import dan200.turtle.api.TurtleAPI;

public class ModuleComputerCraft {

	public static MagnetTurtleUpgrade magnetUpgrade;

	public static void registerAddons() {
		magnetUpgrade = new MagnetTurtleUpgrade();
		TurtleAPI.registerUpgrade(magnetUpgrade);
		if (!OpenBlocks.proxy.isServerOnly()) MinecraftForge.EVENT_BUS.register(magnetUpgrade);
	}
}
