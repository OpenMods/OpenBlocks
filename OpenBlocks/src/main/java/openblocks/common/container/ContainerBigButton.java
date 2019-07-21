package openblocks.common.container;

import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityBigButton;
import openmods.container.ContainerInventoryProvider;

public class ContainerBigButton extends ContainerInventoryProvider<TileEntityBigButton> {

	private static final int UPDATE_TICKS = 0;

	public ContainerBigButton(IInventory playerInventory, TileEntityBigButton button) {
		super(playerInventory, button);
		addInventoryGrid(53, 30, 4);
		addPlayerInventorySlots(100);
	}

	private int totalTicks = 0;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		final int newTotalTicks = owner.getTickTime();
		if (totalTicks != newTotalTicks) {
			totalTicks = newTotalTicks;
			for (IContainerListener listener : listeners)
				listener.sendWindowProperty(this, UPDATE_TICKS, newTotalTicks);
		}
	}

	@Override
	public void updateProgressBar(int id, int data) {
		if (id == UPDATE_TICKS) {
			totalTicks = data;
		} else {
			super.updateProgressBar(id, data);
		}
	}

	public int getTotalTicks() {
		return totalTicks;
	}
}
