package openblocks.integration.cc15;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import openblocks.common.item.MetaMiracleMagnet;
import openblocks.common.item.MetaMiracleMagnet.ITurtleLister;
import openmods.OpenMods;
import openmods.integration.ApiIntegration;
import dan200.turtle.api.TurtleAPI;

public class ModuleTurtlesCC15X extends ApiIntegration {

	public ModuleTurtlesCC15X() {
		super("dan200.turtle.api.ITurtleUpgrade");
	}

	@Override
	public String name() {
		return "OpenBlocks turtles for CC1.5X";
	}

	@Override
	public void load() {
		final MagnetTurtleUpgrade magnetUpgrade = new MagnetTurtleUpgrade();
		TurtleAPI.registerUpgrade(magnetUpgrade);
		if (!OpenMods.proxy.isServerOnly()) MinecraftForge.EVENT_BUS.register(magnetUpgrade);

		MetaMiracleMagnet.lister = new ITurtleLister() {
			@Override
			public void addTurtles(List<ItemStack> result) {
				CCUtils.addUpgradedTurtles(result, magnetUpgrade);

			}
		};
	}

}
