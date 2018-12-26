package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityBlockPlacer;
import openmods.container.ContainerInventoryProvider;

public class ContainerBlockPlacer extends ContainerInventoryProvider<TileEntityBlockPlacer> {
	public ContainerBlockPlacer(IInventory playerInventory, TileEntityBlockPlacer blockPlacer) {
		super(playerInventory, blockPlacer);
		addInventoryGrid(62, 18, 3);
		addPlayerInventorySlots(85);
	}

}
