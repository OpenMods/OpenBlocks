package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openmods.common.container.ContainerInventory;

public class ContainerPaintMixer extends
		ContainerInventory<TileEntityPaintMixer> {

	public ContainerPaintMixer(IInventory playerInventory, TileEntityPaintMixer stainer) {
		super(playerInventory, stainer);
		addSlotToContainer(new RestrictedSlot(owner, 0, 123, 22));
		addSlotToContainer(new RestrictedSlot(owner, 1, 143, 22));
		addSlotToContainer(new RestrictedSlot(owner, 2, 123, 76));
		addSlotToContainer(new RestrictedSlot(owner, 3, 143, 76));
		addSlotToContainer(new RestrictedSlot(owner, 4, 123, 96));
		addSlotToContainer(new RestrictedSlot(owner, 5, 143, 96));
		addPlayerInventorySlots(120);
	}

	@Override
	public void onButtonClicked(EntityPlayer player, int buttonId) {
		getOwner().tryStartMixer();
	}
}
