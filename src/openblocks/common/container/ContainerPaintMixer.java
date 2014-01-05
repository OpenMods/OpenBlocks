package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer.Slots;
import openmods.container.ContainerInventory;

public class ContainerPaintMixer extends
		ContainerInventory<TileEntityPaintMixer> {

	public ContainerPaintMixer(IInventory playerInventory, TileEntityPaintMixer stainer) {
		super(playerInventory, stainer);
		addSlotToContainer(new RestrictedSlot(owner, Slots.paint.ordinal(), 133, 22));
		// addSlotToContainer(new RestrictedSlot(owner, 1, 143, 22));
		addSlotToContainer(new RestrictedSlot(owner, Slots.dyeCyan.ordinal(), 123, 76));
		addSlotToContainer(new RestrictedSlot(owner, Slots.dyeMagenta.ordinal(), 143, 76));
		addSlotToContainer(new RestrictedSlot(owner, Slots.dyeYellow.ordinal(), 123, 96));
		addSlotToContainer(new RestrictedSlot(owner, Slots.dyeBlack.ordinal(), 143, 96));
		addPlayerInventorySlots(120);
	}

	@Override
	public void onButtonClicked(EntityPlayer player, int buttonId) {
		getOwner().tryStartMixer();
	}
}
