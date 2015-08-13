package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openmods.container.ContainerBase;

public class ContainerDevNull extends ContainerBase<Void> {

	private InventoryPlayer playerInventory;

	public ContainerDevNull(IInventory playerInventory, IInventory ownerInventory) {
		super(playerInventory, ownerInventory, null);
		this.playerInventory = (InventoryPlayer)playerInventory;
		addInventoryGrid(80, 22, 1);
		addPlayerInventorySlots(55);
	}

	@Override
	public ItemStack slotClick(int slotId, int key, int modifier, EntityPlayer player) {
		if (modifier == 2 && key == player.inventory.currentItem) return null;
		return super.slotClick(slotId, key, modifier, player);
	}

	@Override
	protected void addPlayerInventorySlots(int offsetX, int offsetY) {
		for (int row = 0; row < 3; row++)
			for (int column = 0; column < 9; column++)
				addSlotToContainer(new Slot(playerInventory,
						column + row * 9 + 9,
						offsetX + column * 18,
						offsetY + row * 18));

		for (int slot = 0; slot < 9; slot++) {

			final int currentSlot = slot;

			addSlotToContainer(new Slot(playerInventory, slot, offsetX + slot * 18, offsetY + 58) {
				@Override
				public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
					return currentSlot != playerInventory.currentItem;
				}
			});

		}
	}

}
