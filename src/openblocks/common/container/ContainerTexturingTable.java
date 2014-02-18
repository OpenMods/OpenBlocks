package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityTexturingTable;
import openmods.container.ContainerInventoryProvider;

public class ContainerTexturingTable extends ContainerInventoryProvider<TileEntityTexturingTable> {

	public ContainerTexturingTable(IInventory playerInventory, TileEntityTexturingTable owner) {
		super(playerInventory, owner);
		addSlotToContainer(new RestrictedSlot(inventory, 0, 130, 26));
		addPlayerInventorySlots(117);
	}

}
