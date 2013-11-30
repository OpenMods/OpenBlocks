package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openblocks.common.item.MetasGeneric;
import openblocks.common.tileentity.TileEntityDrawingTable;
import openmods.container.ContainerInventory;

public class ContainerDrawingTable extends ContainerInventory<TileEntityDrawingTable> {

	public ContainerDrawingTable(IInventory playerInventory, TileEntityDrawingTable table) {
		super(playerInventory, table);
		addSlotToContainer(new RestrictedSlot(owner, 0, 80, 34) {
			@Override
			public boolean isItemValid(ItemStack itemstack) {
				return itemstack == null || MetasGeneric.unpreparedStencil.isA(itemstack);
			}
		});
		addPlayerInventorySlots(90);
	}
}
