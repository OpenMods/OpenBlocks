package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openmods.container.ContainerInventory;

public class ContainerAutoAnvil extends ContainerInventory<TileEntityAutoAnvil> {

	public ContainerAutoAnvil(IInventory playerInventory, TileEntityAutoAnvil tile) {
		super(playerInventory, tile);
		addSlotToContainer(new RestrictedSlot(tile, 0, 14, 40));
		addSlotToContainer(new RestrictedSlot(tile, 1, 56, 40));
		addSlotToContainer(new RestrictedSlot(tile, 2, 110, 40));
		addPlayerInventorySlots(93);
		tile.sync();
	}

}
