package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityItemDropper;
import openmods.container.ContainerInventoryProvider;

public class ContainerItemDropper extends ContainerInventoryProvider<TileEntityItemDropper> {
	public ContainerItemDropper(IInventory playerInventory, TileEntityItemDropper itemDropper) {
		super(playerInventory, itemDropper);
		addInventoryGrid(8, 18, 3);
		addPlayerInventorySlots(300 / 2 - (9 * 18 + 9) / 2, 85);
	}

}
