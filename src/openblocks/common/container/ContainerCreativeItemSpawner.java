package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityCreativeItemSpawner;
import openmods.container.ContainerInventoryProvider;

public class ContainerCreativeItemSpawner extends ContainerInventoryProvider<TileEntityCreativeItemSpawner> {

	public ContainerCreativeItemSpawner(IInventory playerInventory, TileEntityCreativeItemSpawner owner) {
		super(playerInventory, owner);
		addInventoryGrid(15, 38, 2);
		addPlayerInventorySlots(108);
		owner.sync();
	}

}
