package openblocks.integration;

import net.minecraftforge.common.MinecraftForge;
import openmods.OpenMods;
import openperipheral.api.OpenPeripheralAPI;
import dan200.turtle.api.TurtleAPI;

public class ModuleOpenPeripheral {

	public static MagnetTurtleUpgrade magnetUpgrade;

	public static void registerMagnetTurtle() {
		magnetUpgrade = new MagnetTurtleUpgrade();
		TurtleAPI.registerUpgrade(magnetUpgrade);
		if (!OpenMods.proxy.isServerOnly()) MinecraftForge.EVENT_BUS.register(magnetUpgrade);
	}

	public static void registerAdapters() {
		OpenPeripheralAPI.register(new AdapterVillageHighlighter());
		OpenPeripheralAPI.register(new AdapterDonationStation());
		OpenPeripheralAPI.register(new AdapterCannon());
		OpenPeripheralAPI.register(new AdapterProjector());
	}
}
