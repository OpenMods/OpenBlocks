package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;

public class ContainerAutoEnchantmentTable extends
		ContainerInventory<TileEntityAutoEnchantmentTable> {

	public ContainerAutoEnchantmentTable(IInventory playerInventory, TileEntityAutoEnchantmentTable tile) {
		super(playerInventory, tile);
		addSlotToContainer(new RestrictedSlot(tile, 0, 18, 40));
		addSlotToContainer(new RestrictedSlot(tile, 1, 100, 40));
		addPlayerInventorySlots(93);
		tile.sync();
	}
}
