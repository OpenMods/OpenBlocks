package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openblocks.common.item.ItemGeneric;
import openblocks.common.tileentity.TileEntityDrawingTable;

public class ContainerDrawingTable extends ContainerInventory<TileEntityDrawingTable> {

	public ContainerDrawingTable(IInventory playerInventory, TileEntityDrawingTable table) {
		super(playerInventory, table);
		addSlotToContainer(new RestrictedSlot(owner, 0, 60, 55) {
			@Override
			public boolean isItemValid(ItemStack itemstack) {
				return itemstack.isItemEqual(ItemGeneric.Metas.unpreparedStencil.newItemStack());
			}
		});
		addPlayerInventorySlots(90);
	}

	
}
