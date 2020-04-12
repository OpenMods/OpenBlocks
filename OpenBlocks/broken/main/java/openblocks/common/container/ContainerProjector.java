package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openblocks.common.item.ItemHeightMap;
import openblocks.common.tileentity.TileEntityProjector;
import openmods.container.ContainerInventoryProvider;

public class ContainerProjector extends ContainerInventoryProvider<TileEntityProjector> {

	public ContainerProjector(IInventory playerInventory, TileEntityProjector owner) {
		super(playerInventory, owner);
		addSlotToContainer(new RestrictedSlot(inventory, 0, 79, 130));
		addPlayerInventorySlots(8, 152);
	}

	public Integer getMapId() {
		ItemStack map = inventory.getStackInSlot(0);
		if (!map.isEmpty() && map.getItem() instanceof ItemHeightMap) return ItemHeightMap.getMapId(map);

		return null;
	}

	public int rotation() {
		return owner.rotation();
	}
}
