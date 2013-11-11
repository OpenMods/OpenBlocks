package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityPaintMixer;

public class ContainerPaintMixer extends
		ContainerInventory<TileEntityPaintMixer> {

	public ContainerPaintMixer(IInventory playerInventory, TileEntityPaintMixer stainer) {
		super(playerInventory, stainer);
		addSlotToContainer(new RestrictedSlot(inventory, 0, 123, 22));
		addSlotToContainer(new RestrictedSlot(inventory, 1, 143, 22));
		addSlotToContainer(new RestrictedSlot(inventory, 2, 123, 76));
		addSlotToContainer(new RestrictedSlot(inventory, 3, 143, 76));
		addSlotToContainer(new RestrictedSlot(inventory, 4, 123, 96));
		addSlotToContainer(new RestrictedSlot(inventory, 5, 143, 96));
		addPlayerInventorySlots(120);
	}

	@Override
	public void onButtonClicked(EntityPlayer player, int buttonId) {
		getOwner().tryStartMixer();
	}
}
