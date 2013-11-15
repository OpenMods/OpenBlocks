package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openblocks.common.item.ItemHeightMap;
import openblocks.common.tileentity.TileEntityProjector;

public class ContainerProjector extends ContainerInventory<TileEntityProjector> {

	public ContainerProjector(IInventory playerInventory, TileEntityProjector owner) {
		super(playerInventory, owner);
		addSlotToContainer(new RestrictedSlot(owner, 0, 79, 130));
		addPlayerInventorySlots(8, 152);
	}

	public Integer getMapId() {
		ItemStack map = getOwner().getStackInSlot(0);

		if (map != null && map.getItem() instanceof ItemHeightMap) return map.getItemDamage();

		return null;
	}

	public void rotate(int rotation) {
		owner.rotate(rotation);
	}

	public int rotation() {
		return owner.rotation();
	}
}
