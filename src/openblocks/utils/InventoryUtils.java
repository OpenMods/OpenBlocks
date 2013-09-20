package openblocks.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class InventoryUtils {

	public static void tryMergeStacks(IInventory targetInventory, int slot, ItemStack stack) {
		if (targetInventory.isItemValidForSlot(slot, stack)) {
			ItemStack targetStack = targetInventory.getStackInSlot(slot);
			if (targetStack == null) {
				targetInventory.setInventorySlotContents(slot, stack.copy());
				stack.stackSize = 0;
			} else {
				boolean valid = targetInventory.isItemValidForSlot(slot, stack);
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

	public static int getSlotIndexOfNextStack(IInventory invent) {
		for (int i = 0; i < invent.getSizeInventory(); i++) {
			ItemStack stack = invent.getStackInSlot(i);
			if (stack != null) {
				return i;
			}
		}
		return -1;
	}

	public static ItemStack removeNextItemStack(IInventory invent) {
		int nextFilledSlot = getSlotIndexOfNextStack(invent);
		if (nextFilledSlot > -1) {
			ItemStack copy = invent.getStackInSlot(nextFilledSlot).copy();
			invent.setInventorySlotContents(nextFilledSlot, null);
			return copy;
		}
		return null;
	}

	public static IInventory getInventory(World world, int x, int y, int z, ForgeDirection direction) {
		if (direction != null && direction != ForgeDirection.UNKNOWN) {
			x += direction.offsetX;
			y += direction.offsetY;
			z += direction.offsetZ;
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if ((tileEntity != null) && ((tileEntity instanceof IInventory)))
			{
				int blockID = world.getBlockId(x, y, z);
				Block block = Block.blocksList[blockID];
				if ((block instanceof BlockChest))
				{
					if (world.getBlockId(x - 1, y, z) == blockID) { return new InventoryLargeChest("Large chest", (IInventory)world.getBlockTileEntity(x - 1, y, z), (IInventory)tileEntity); }
					if (world.getBlockId(x + 1, y, z) == blockID) { return new InventoryLargeChest("Large chest", (IInventory)tileEntity, (IInventory)world.getBlockTileEntity(x + 1, y, z)); }
					if (world.getBlockId(x, y, z - 1) == blockID) { return new InventoryLargeChest("Large chest", (IInventory)world.getBlockTileEntity(x, y, z - 1), (IInventory)tileEntity); }
					if (world.getBlockId(x, y, z + 1) == blockID) { return new InventoryLargeChest("Large chest", (IInventory)tileEntity, (IInventory)world.getBlockTileEntity(x, y, z + 1)); }
				}
				return (IInventory)tileEntity;
			}
		}else {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te instanceof IInventory) {
				return (IInventory) te;
			}
		}
		return null;
	}
	

	/**
	 * Tests to see if an item stack can be inserted in to an inventory
	 * Does not perform the insertion, only tests the possibility
	 *
	 * @param inventory
	 *            The inventory to insert the stack into
	 * @param item
	 *            the stack to insert
	 * @return the amount of items that could be put in to the stack
	 */
	public static int testInventoryInsertion(IInventory inventory, ItemStack item) {
		if (item == null || item.stackSize == 0) return 0;
		if (inventory == null) return 0;
		int slotCount = inventory.getSizeInventory();
		/*
		 * Allows counting down the item size, without cloning or changing the
		 * object
		 */
		int itemSizeCounter = item.stackSize;
		for (int i = 0; i < slotCount && itemSizeCounter > 0; i++) {
			
			if (!inventory.isItemValidForSlot(i, item)) continue;
			ItemStack inventorySlot = inventory.getStackInSlot(i);
			/*
			 * If the slot is empty, dump the biggest stack we can, taking in to
			 * consideration, the remaining amount of stack
			 */
			if (inventorySlot == null) {
				itemSizeCounter -= Math.min(Math.min(itemSizeCounter, inventory.getInventoryStackLimit()), item.getMaxStackSize());
			}
			/* If the slot is not empty, check that these items stack */
			else if (item.itemID == inventorySlot.itemID
					&& (!item.getHasSubtypes() || item.getItemDamage() == inventorySlot.getItemDamage())
					&& ItemStack.areItemStackTagsEqual(item, inventorySlot)
					&& inventorySlot.stackSize < inventorySlot.getMaxStackSize()) {
				/* If they stack, decrement by the amount of space that remains */
				
				int space = inventorySlot.getMaxStackSize()
						- inventorySlot.stackSize;
				itemSizeCounter -= Math.min(itemSizeCounter, space);
			}
		}
		// itemSizeCounter might be less than zero here. It shouldn't be, but I
		// don't trust me. -NC
		if (itemSizeCounter != item.stackSize) {
			itemSizeCounter = Math.max(itemSizeCounter, 0);
			return item.stackSize - itemSizeCounter;
		}
		return 0;
	}
}
