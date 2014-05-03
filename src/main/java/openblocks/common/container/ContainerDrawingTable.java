package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityDrawingTable;
import openmods.container.ContainerInventoryProvider;

public class ContainerDrawingTable extends ContainerInventoryProvider<TileEntityDrawingTable> {

	public ContainerDrawingTable(IInventory playerInventory, TileEntityDrawingTable table) {
		super(playerInventory, table);
		addSlotToContainer(new RestrictedSlot(inventory, 0, 80, 34));
		addPlayerInventorySlots(90);
	}
}
