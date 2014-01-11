package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityItemDropper;
import openmods.container.ContainerInventoryProvider;

public class ContainerItemDropper extends ContainerInventoryProvider<TileEntityItemDropper> {
	public ContainerItemDropper(IInventory playerInventory, TileEntityItemDropper itemDropper) {
		super(playerInventory, itemDropper);
		addInventoryGrid(62, 18, 3);
		addPlayerInventorySlots(85);
	}

}
