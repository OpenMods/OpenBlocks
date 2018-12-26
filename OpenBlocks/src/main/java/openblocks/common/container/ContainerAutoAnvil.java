package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openmods.container.ContainerInventoryProvider;

public class ContainerAutoAnvil extends ContainerInventoryProvider<TileEntityAutoAnvil> {

	public ContainerAutoAnvil(IInventory playerInventory, TileEntityAutoAnvil tile) {
		super(playerInventory, tile);
		addSlotToContainer(new RestrictedSlot(inventory, 0, 14, 40));
		addSlotToContainer(new RestrictedSlot(inventory, 1, 56, 40));
		addSlotToContainer(new RestrictedSlot(inventory, 2, 110, 40));
		addPlayerInventorySlots(93);
	}

}
