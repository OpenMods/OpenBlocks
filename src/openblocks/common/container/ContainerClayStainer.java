package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityClayStainer;

public class ContainerClayStainer extends ContainerInventory<TileEntityClayStainer> {

	public ContainerClayStainer(IInventory playerInventory, TileEntityClayStainer stainer) {
		super(playerInventory, stainer);
		addSlotToContainer(new RestrictedSlot(inventory, 0, 133, 36));
		addSlotToContainer(new RestrictedSlot(inventory, 1, 115, 72));
		addSlotToContainer(new RestrictedSlot(inventory, 2, 133, 72));
		addSlotToContainer(new RestrictedSlot(inventory, 3, 151, 72));
		addPlayerInventorySlots(110);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer pl, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot)inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			int qty = itemstack1.stackSize;
			if (itemstack1.getItem() == TileEntityClayStainer.HARDENED_CLAY.getItem() ||
				itemstack1.getItem() == TileEntityClayStainer.STAINED_CLAY.getItem()) {
				itemstack1 = new ItemStack(OpenBlocks.Blocks.specialStainedClay);
				itemstack1.stackSize = qty;
			}
			itemstack = itemstack1.copy();
			if (i < inventorySize) {
				if (!mergeItemStack(itemstack1, inventorySize, inventorySlots.size(), true)) return null;
			} else if (!mergeItemStack(itemstack1, 0, inventorySize, false)) return null;
			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}
	
	@Override
    protected boolean mergeItemStack(ItemStack itemStack, int par2, int par3, boolean par4) {
		if (par2 == 0 && itemStack.isItemEqual(new ItemStack(OpenBlocks.Blocks.specialStainedClay))) {
			ItemStack currentStack = ((Slot)inventorySlots.get(0)).getStack();
			if (currentStack != null) {
				itemStack.setTagCompound(currentStack.getTagCompound());
			}
		}
		return super.mergeItemStack(itemStack, par2, par3, par4);
    }

}
