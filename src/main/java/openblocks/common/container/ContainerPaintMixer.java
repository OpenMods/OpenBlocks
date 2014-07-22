package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer.Slots;
import openmods.container.ContainerInventoryProvider;

public class ContainerPaintMixer extends ContainerInventoryProvider<TileEntityPaintMixer> {

	public ContainerPaintMixer(IInventory playerInventory, TileEntityPaintMixer stainer) {
		super(playerInventory, stainer);
		addSlotToContainer(new RestrictedSlot(inventory, Slots.paint.ordinal(), 133, 22));
		// addSlotToContainer(new RestrictedSlot(owner, 1, 143, 22));
		addSlotToContainer(new RestrictedSlot(inventory, Slots.dyeCyan.ordinal(), 123, 76));
		addSlotToContainer(new RestrictedSlot(inventory, Slots.dyeMagenta.ordinal(), 143, 76));
		addSlotToContainer(new RestrictedSlot(inventory, Slots.dyeYellow.ordinal(), 123, 96));
		addSlotToContainer(new RestrictedSlot(inventory, Slots.dyeBlack.ordinal(), 143, 96));
		addPlayerInventorySlots(120);
	}
}
