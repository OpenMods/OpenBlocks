package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntitySprinkler;
import openmods.container.ContainerInventoryProvider;

public class ContainerSprinkler extends ContainerInventoryProvider<TileEntitySprinkler> {

	public ContainerSprinkler(IInventory playerInventory, TileEntitySprinkler sprinkler) {
		super(playerInventory, sprinkler);
		addInventoryGrid(62, 18, 3);
		addPlayerInventorySlots(85);
	}

}
