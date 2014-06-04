package openblocks.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import openblocks.client.Icons;
import openblocks.common.item.MetasGeneric;
import openperipheral.api.IUpdateHandler;
import openperipheral.api.cc16.ComputerCraftWrappers;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;

public class MagnetTurtleUpgrade implements ITurtleUpgrade {
	public IIcon icon;

	@Override
	public int getUpgradeID() {
		return TurtleIds.MAGNET_TURTLE_ID;
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "openblocks.turtle.magnet";
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
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return ComputerCraftWrappers.createPeripheral(new MagnetControlAdapter(turtle, side));
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return null;
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return icon;
	}

	@SubscribeEvent
	public void registerIcons(TextureStitchEvent evt) {
		if (evt.map.getTextureType() == Icons.ICON_TYPE_BLOCK) icon = evt.map.registerIcon("openblocks:magnet_upgrade");
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
		IPeripheral peripheral = turtle.getPeripheral(side);
		if (peripheral instanceof IUpdateHandler) ((IUpdateHandler)peripheral).onPeripheralUpdate();
	}

}
