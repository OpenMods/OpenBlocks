package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openmods.container.ContainerInventoryProvider;

public class ContainerAutoEnchantmentTable extends ContainerInventoryProvider<TileEntityAutoEnchantmentTable> {

	public ContainerAutoEnchantmentTable(IInventory playerInventory, TileEntityAutoEnchantmentTable tile) {
		super(playerInventory, tile);
		addSlotToContainer(new RestrictedSlot(inventory, TileEntityAutoEnchantmentTable.Slots.tool.ordinal(), 18, 20));
		addSlotToContainer(new RestrictedSlot(inventory, TileEntityAutoEnchantmentTable.Slots.lapis.ordinal(), 18, 40));
		addSlotToContainer(new RestrictedSlot(inventory, TileEntityAutoEnchantmentTable.Slots.output.ordinal(), 100, 40));
		addPlayerInventorySlots(93);
	}
}
