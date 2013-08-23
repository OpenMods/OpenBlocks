package openblocks.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {

	public static void tryMergeStacks(IInventory targetInventory, int slot, ItemStack stack) {
		if (targetInventory.isStackValidForSlot(slot, stack)) {
			ItemStack targetStack = targetInventory.getStackInSlot(slot);
			if (targetStack == null) {
				targetInventory.setInventorySlotContents(slot, stack.copy());
				stack.stackSize = 0;
			} else {
				boolean valid = targetInventory.isStackValidForSlot(slot, stack);
				if (valid
						&& stack.itemID == targetStack.itemID
						&& (!stack.getHasSubtypes() || stack.getItemDamage() == targetStack.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stack, targetStack)
						&& targetStack.stackSize < targetStack.getMaxStackSize()) {
					int space = targetStack.getMaxStackSize()
							- targetStack.stackSize;
					int mergeAmount = Math.min(space, stack.stackSize);
					ItemStack copy = targetStack.copy();
					copy.stackSize += mergeAmount;
					targetInventory.setInventorySlotContents(slot, copy);
					stack.stackSize -= mergeAmount;
				}
			}
		}
	}

	public static void insertItemIntoInventory(IInventory inventory, ItemStack stack) {
		int i = 0;
		while (stack.stackSize > 0 && i < inventory.getSizeInventory()) {
			tryMergeStacks(inventory, i, stack);
			i++;
		}
	}

	public static int moveItemInto(IInventory fromInventory, int slot, IInventory targetInventory, int intoSlot, int maxAmount) {
		int merged = 0;
		ItemStack stack = fromInventory.getStackInSlot(slot);
		if (stack == null) { return merged; }
		ItemStack clonedStack = stack.copy();
		clonedStack.stackSize = Math.min(clonedStack.stackSize, maxAmount);
		int amountToMerge = clonedStack.stackSize;
		InventoryUtils.tryMergeStacks(targetInventory, intoSlot, clonedStack);
		merged = (amountToMerge - clonedStack.stackSize);
		fromInventory.decrStackSize(slot, merged);
		return merged;
	}

	public static int moveItem(IInventory fromInventory, int slot, IInventory targetInventory, int maxAmount) {
		int merged = 0;
		ItemStack stack = fromInventory.getStackInSlot(slot);
		if (stack == null) { return 0; }
		ItemStack clonedStack = stack.copy();
		clonedStack.stackSize = Math.min(clonedStack.stackSize, maxAmount);
		int amountToMerge = clonedStack.stackSize;
		InventoryUtils.insertItemIntoInventory(targetInventory, clonedStack);
		merged = (amountToMerge - clonedStack.stackSize);
		fromInventory.decrStackSize(slot, merged);
		return merged;
	}
	
    public static boolean consumeInventoryItem(IInventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
        	ItemStack stackInSlot = inventory.getStackInSlot(i);
        	if (stackInSlot != null && stackInSlot.isItemEqual(stack)) {
        		stackInSlot.stackSize--;
            	if (stackInSlot.stackSize == 0) {
            		inventory.setInventorySlotContents(i, null);
            	}
            	return true;
        	}
        }
        return false;
    }
}
