package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityItemDropper;

public class ContainerItemDropper extends ContainerInventory<TileEntityItemDropper> {
	public ContainerItemDropper(IInventory playerInventory, TileEntityItemDropper itemDropper) {
		super(playerInventory, itemDropper);
		addInventoryGrid(62, 18, 3);
		addPlayerInventorySlots(85);
	}

}
