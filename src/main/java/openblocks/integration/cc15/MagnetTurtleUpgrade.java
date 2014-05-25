package openblocks.integration.cc15;

import javax.swing.Icon;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.TextureStitchEvent;
import openblocks.client.Icons;
import openblocks.common.item.MetasGeneric;
import openblocks.integration.TurtleIds;
import openperipheral.api.cc16.ComputerCraftWrappers;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dan200.computercraft.api.turtle.*;

public class MagnetTurtleUpgrade implements ITurtleUpgrade {

	public Icon icon;

	@Override
	public int getUpgradeID() {
		return TurtleIds.MAGNET_TURTLE_ID;
	}

	@Override
	public String getAdjective() {
		return StatCollector.translateToLocal("openblocks.turtle.magnet");
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return MetasGeneric.miracleMagnet.newItemStack();
	}

	@Override
	public boolean isSecret() {
		return false;
	}

	@Override
	public IHostedPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return ComputerCraftWrappers.createHostedPeripheral(new MagnetControlAdapter(turtle, side));
	}

	@Override
	public boolean useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return false;
	}

	@Override
	public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return icon;
	}

	@SubscribeEvent
	public void registerIcons(TextureStitchEvent evt) {
		if (evt.map.getTextureType() == Icons.ICON_TYPE_BLOCK) icon = evt.map.registerIcon("openblocks:magnet_upgrade");
	}

}
