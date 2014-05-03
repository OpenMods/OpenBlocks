package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityBigButton;
import openmods.container.ContainerInventoryProvider;

public class ContainerBigButton extends ContainerInventoryProvider<TileEntityBigButton> {

	public ContainerBigButton(IInventory playerInventory, TileEntityBigButton button) {
		super(playerInventory, button);
		addInventoryGrid(80, 23, 1);
		addPlayerInventorySlots(60);
	}
}
