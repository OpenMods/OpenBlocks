package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openblocks.common.tileentity.TileEntitySprinkler;
import openblocks.common.tileentity.TileEntityVacuumHopper;

public class ContainerVacuumHopper extends Container {

	protected int inventorySize;
	protected IInventory playerInventory;
	private TileEntityVacuumHopper hopper;

	public ContainerVacuumHopper(IInventory playerInventory, TileEntityVacuumHopper sprinkler) {
		this.inventorySize = sprinkler.getSizeInventory();
		this.playerInventory = playerInventory;
		this.hopper = sprinkler;
		addInventoryGrid(62, 18, 3);
		addPlayerInventorySlots(85);
	}

	public void addInventorySlot(int slotId, int x, int y) {
		addSlotToContainer(new Slot(hopper, slotId, x, y));
	}

	protected void addInventoryGrid(int xOffset, int yOffset, int width) {
		int height = (int)Math.ceil((double)inventorySize / width);
		for (int y = 0, slotId = 0; y < height; y++) {
			for (int x = 0; x < width; x++, slotId++) {
				addSlotToContainer(new Slot(hopper, slotId, xOffset + x * 18, yOffset
						+ y * 18));
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
		return hopper.isUseableByPlayer(entityplayer);
	}

	public TileEntityVacuumHopper getTileEntity() {
		return hopper;
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

}
