package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntitySprinkler;
import openmods.common.container.ContainerInventory;

public class ContainerSprinkler extends ContainerInventory<TileEntitySprinkler> {

	public ContainerSprinkler(IInventory playerInventory, TileEntitySprinkler sprinkler) {
		super(playerInventory, sprinkler);
		addInventoryGrid(62, 18, 3);
		addPlayerInventorySlots(85);
	}

}
