package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityDrawingTable;
import openmods.container.ContainerInventoryProvider;

public class ContainerDrawingTable extends ContainerInventoryProvider<TileEntityDrawingTable> {

	public ContainerDrawingTable(IInventory playerInventory, TileEntityDrawingTable table) {
		super(playerInventory, table);
		addSlotToContainer(new RestrictedSlot(inventory, TileEntityDrawingTable.SLOT_INPUT, 88 - 8 - 17, 34));
		addSlotToContainer(new RestrictedSlot(inventory, TileEntityDrawingTable.SLOT_OUTPUT, 88 + 8 + 1, 34));
		addPlayerInventorySlots(122);
	}
}
