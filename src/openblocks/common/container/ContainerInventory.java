package openblocks.common.container;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerInventory<T extends IInventory> extends
		Container {

	protected final int inventorySize;
	protected final IInventory playerInventory;
	private final T inventory;

	protected static class RestrictedSlot extends Slot {

		public RestrictedSlot(IInventory inventory, int slot, int x, int y) {
			super(inventory, slot, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack itemstack) {
			return inventory.isItemValidForSlot(slotNumber, itemstack);
		}
	}

	public ContainerInventory(IInventory playerInventory, T entity) {
		this.inventorySize = entity.getSizeInventory();
		this.playerInventory = playerInventory;
		this.inventory = entity;
	}

	protected void addInventoryGrid(int xOffset, int yOffset, int width) {
		int height = (int)Math.ceil((double)inventorySize / width);
		for (int y = 0, slotId = 0; y < height; y++) {
			for (int x = 0; x < width; x++, slotId++) {
				addSlotToContainer(new RestrictedSlot(inventory, slotId, xOffset
						+ x * 18, yOffset + y * 18));
			}
		}
	}

	protected void addPlayerInventorySlots(int offsetY) {
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 9; k1++) {
				addSlotToContainer(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, offsetY
						+ l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; i1++) {
			addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, offsetY + 58));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	public T getTileEntity() {
		return inventory;
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

	public abstract void onServerButtonClicked(EntityPlayer player, int buttonId);

	public abstract void onClientButtonClicked(int buttonId);

	public void sendButtonClick(int buttonId) {
		onClientButtonClicked(buttonId);
		Minecraft.getMinecraft().playerController.sendEnchantPacket(this.windowId, buttonId);
	}

	@Override
	public boolean enchantItem(EntityPlayer player, int button) {
		onServerButtonClicked(player, button);
		return false;
	}
}
