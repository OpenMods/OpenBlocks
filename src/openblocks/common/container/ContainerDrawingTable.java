package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityDrawingTable;

public class ContainerDrawingTable extends ContainerInventory<TileEntityDrawingTable> {

	public ContainerDrawingTable(IInventory playerInventory, TileEntityDrawingTable table) {
		super(playerInventory, table);
		addSlotToContainer(new RestrictedSlot(owner, 0, 79, 130));
		addPlayerInventorySlots(69);
	}

}
