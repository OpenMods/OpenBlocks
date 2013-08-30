package openblocks.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import openblocks.common.api.IInventoryCallback;

public class GenericInventory implements IInventory, ISidedInventory {

	protected List<IInventoryCallback> callbacks;
	protected String inventoryTitle;
	protected int slotsCount;
	protected ItemStack[] inventoryContents;
	protected boolean isInvNameLocalized;

	public GenericInventory(String name, boolean isInvNameLocalized, int size) {
		callbacks = new ArrayList<IInventoryCallback>();
		this.isInvNameLocalized = isInvNameLocalized;
		this.slotsCount = size;
		this.inventoryTitle = name;
		this.inventoryContents = new ItemStack[size];
	}

	public void addCallback(IInventoryCallback callback) {
		callbacks.add(callback);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void closeChest() {}

	@Override
	public ItemStack decrStackSize(int stackIndex, int byAmount) {
		if (this.inventoryContents[stackIndex] != null) {
			ItemStack itemstack;

			if (this.inventoryContents[stackIndex].stackSize <= byAmount) {
				itemstack = this.inventoryContents[stackIndex];
				this.inventoryContents[stackIndex] = null;
				this.onInventoryChanged(stackIndex);
				return itemstack;
			} else {
				itemstack = this.inventoryContents[stackIndex].splitStack(byAmount);

				if (this.inventoryContents[stackIndex].stackSize == 0) {
					this.inventoryContents[stackIndex] = null;
				}

				this.onInventoryChanged(stackIndex);
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public String getInvName() {
		return this.inventoryTitle;
	}

	@Override
	public int getSizeInventory() {
		return slotsCount;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.inventoryContents[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.inventoryContents[i] != null) {
			ItemStack itemstack = this.inventoryContents[i];
			this.inventoryContents[i] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public boolean isInvNameLocalized() {
		return isInvNameLocalized;
	}

	public boolean isItem(int slot, Item item) {
		return inventoryContents[slot] != null
				&& inventoryContents[slot].getItem() == item;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	public void onInventoryChanged(int slotNumber) {
		for (int i = 0; i < callbacks.size(); ++i) {
			callbacks.get(i).onInventoryChanged(this, slotNumber);
		}
	}

	@Override
	public void openChest() {}

	public void clearAndSetSlotCount(int amount) {
		this.slotsCount = amount;
		inventoryContents = new ItemStack[amount];
	}

	public void readFromNBT(NBTTagCompound tag) {

		if (tag.hasKey("size")) {
			this.slotsCount = tag.getInteger("size");
		}
		NBTTagList nbttaglist = tag.getTagList("Items");
		inventoryContents = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound stacktag = (NBTTagCompound)nbttaglist.tagAt(i);
			int j = stacktag.getByte("Slot") & 0xff;
			if (j >= 0 && j < inventoryContents.length) {
				inventoryContents[j] = ItemStack.loadItemStackFromNBT(stacktag);
			}
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.inventoryContents[i] = itemstack;

		if (itemstack != null
				&& itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged(i);
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("size", getSizeInventory());
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventoryContents.length; i++) {
			if (inventoryContents[i] != null) {
				NBTTagCompound stacktag = new NBTTagCompound();
				stacktag.setByte("Slot", (byte)i);
				inventoryContents[i].writeToNBT(stacktag);
				nbttaglist.appendTag(stacktag);
			}
		}
		tag.setTag("Items", nbttaglist);
	}

	/**
	 * This bastard never even gets called, so ignore it
	 */
	@Override
	public void onInventoryChanged() {}

	public void copyFrom(IInventory inventory) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (i < getSizeInventory()) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack != null) {
					setInventorySlotContents(i, stack.copy());
				} else {
					setInventorySlotContents(i, null);
				}
			}
		}
	}
}
