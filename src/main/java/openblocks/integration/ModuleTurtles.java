package openblocks.integration;

import static openmods.conditions.Conditions.all;
import static openmods.integration.Conditions.classExists;
import static openmods.integration.Conditions.modLoaded;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.common.MinecraftForge;
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
			final MagnetTurtleUpgrade magnetUpgrade = new MagnetTurtleUpgrade();
			ComputerCraftAPI.registerTurtleUpgrade(magnetUpgrade);
			if (FMLCommonHandler.instance().getSide() == Side.CLIENT) MinecraftForge.EVENT_BUS.register(magnetUpgrade);
		}
	}

}
