package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openblocks.common.item.ItemGeneric;
import openblocks.common.tileentity.TileEntityDrawingTable;
import openmods.container.ContainerInventory;

public class ContainerDrawingTable extends ContainerInventory<TileEntityDrawingTable> {

	public ContainerDrawingTable(IInventory playerInventory, TileEntityDrawingTable table) {
		super(playerInventory, table);
		addSlotToContainer(new RestrictedSlot(owner, 0, 80, 34) {
			@Override
			public boolean isItemValid(ItemStack itemstack) {
				return itemstack == null || ItemGeneric.isA(itemstack, ItemGeneric.Metas.unpreparedStencil);
			}
		});
		addPlayerInventorySlots(90);
	}

	
}
