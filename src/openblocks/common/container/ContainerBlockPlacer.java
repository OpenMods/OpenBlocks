package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityBlockPlacer;
import openmods.common.container.ContainerInventory;

public class ContainerBlockPlacer extends
		ContainerInventory<TileEntityBlockPlacer> {
	public ContainerBlockPlacer(IInventory playerInventory, TileEntityBlockPlacer blockPlacer) {
		super(playerInventory, blockPlacer);
		addInventoryGrid(62, 18, 3);
		addPlayerInventorySlots(85);
	}

}
