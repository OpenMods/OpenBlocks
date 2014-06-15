package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityXPBottler;
import openmods.container.ContainerInventoryProvider;

public class ContainerXPBottler extends ContainerInventoryProvider<TileEntityXPBottler> {

	public ContainerXPBottler(IInventory playerInventory, TileEntityXPBottler xpbottler) {
		super(playerInventory, xpbottler);
		addSlotToContainer(new RestrictedSlot(inventory, 0, 48, 30));
		addSlotToContainer(new RestrictedSlot(inventory, 1, 110, 30));
		addPlayerInventorySlots(69);
	}

}
