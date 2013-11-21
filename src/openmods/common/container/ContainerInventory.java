package openmods.common.container;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openmods.common.tileentity.SyncedTileEntity;

public abstract class ContainerInventory<T extends IInventory> extends
		Container {

	protected final int inventorySize;
	protected final IInventory playerInventory;
	protected final T owner;

	protected static class RestrictedSlot extends Slot {

		public RestrictedSlot(IInventory inventory, int slot, int x, int y) {
			super(inventory, slot, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack itemstack) {
			return inventory.isItemValidForSlot(slotNumber, itemstack);
		}
	}

	public ContainerInventory(IInventory playerInventory, T owner) {
		this.inventorySize = owner.getSizeInventory();
		this.playerInventory = playerInventory;
		this.owner = owner;
	}

	protected void addInventoryGrid(int xOffset, int yOffset, int width) {
		int height = (int)Math.ceil((double)inventorySize / width);
		for (int y = 0, slotId = 0; y < height; y++) {
			for (int x = 0; x < width; x++, slotId++) {
				addSlotToContainer(new RestrictedSlot(owner, slotId,
						xOffset + x * 18,
						yOffset + y * 18));
			}
		}
	}

	protected void addPlayerInventorySlots(int offsetY) {
		addPlayerInventorySlots(8, offsetY);
	}

	protected void addPlayerInventorySlots(int offsetX, int offsetY) {
		for (int row = 0; row < 3; row++)
			for (int column = 0; column < 9; column++)
				addSlotToContainer(new Slot(playerInventory,
						column + row * 9 + 9,
						offsetX + column * 18,
						offsetY + row * 18));

		for (int slot = 0; slot < 9; slot++)
			addSlotToContainer(new Slot(playerInventory, slot, offsetX + slot
					* 18, offsetY + 58));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return owner.isUseableByPlayer(entityplayer);
	}

	public T getOwner() {
		return owner;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer pl, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot)inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
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

	public int getInventorySize() {
		return inventorySize;
	}

	@SuppressWarnings("unchecked")
	public Set<EntityPlayer> getPlayers() {
		Set<EntityPlayer> players = new HashSet<EntityPlayer>();
		for (ICrafting crafter : (List<ICrafting>)crafters) {
			if (crafter instanceof EntityPlayerMP) {
				players.add((EntityPlayerMP)crafter);
			}
		}
		return players;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		T te = getOwner();
		if (te instanceof SyncedTileEntity) {
			((SyncedTileEntity)te).sync();
		}
	}

	public void onButtonClicked(EntityPlayer player, int buttonId) {}

	@Override
	public boolean enchantItem(EntityPlayer player, int buttonId) {
		onButtonClicked(player, buttonId);
		return false;
	}

}
