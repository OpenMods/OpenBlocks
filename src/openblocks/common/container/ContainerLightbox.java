package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityLightbox;

public class ContainerLightbox extends ContainerInventory<TileEntityLightbox> {

	public ContainerLightbox(IInventory playerInventory,
			TileEntityLightbox lightbox) {
		super(playerInventory, lightbox);
		addInventoryGrid(80, 23, 1);
		addPlayerInventorySlots(60);
	}

}
