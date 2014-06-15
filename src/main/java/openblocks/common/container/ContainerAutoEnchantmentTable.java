package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openmods.container.ContainerInventoryProvider;

public class ContainerAutoEnchantmentTable extends ContainerInventoryProvider<TileEntityAutoEnchantmentTable> {

	public ContainerAutoEnchantmentTable(IInventory playerInventory, TileEntityAutoEnchantmentTable tile) {
		super(playerInventory, tile);
		addSlotToContainer(new RestrictedSlot(inventory, 0, 18, 40));
		addSlotToContainer(new RestrictedSlot(inventory, 1, 100, 40));
		addPlayerInventorySlots(93);
	}
}
