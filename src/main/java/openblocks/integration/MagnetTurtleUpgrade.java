package openblocks.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import openblocks.common.item.MetasGeneric;
import openmods.utils.TextureUtils;
import openperipheral.api.ApiHolder;
import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;

public class MagnetTurtleUpgrade implements ITurtleUpgrade {
	@ApiHolder
	private static IComputerCraftObjectsFactory ccFactory;

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
		return ccFactory.createPeripheral(new MagnetControlAdapter(turtle, side));
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
		if (evt.map.getTextureType() == TextureUtils.TEXTURE_MAP_BLOCKS) icon = evt.map.registerIcon("openblocks:magnet_upgrade");
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
		IPeripheral peripheral = turtle.getPeripheral(side);
		if (peripheral instanceof ITickingTurtle) ((ITickingTurtle)peripheral).onPeripheralTick();
	}

}
