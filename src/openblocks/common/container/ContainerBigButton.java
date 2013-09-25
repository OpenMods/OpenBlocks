package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityBigButton;

public class ContainerBigButton extends ContainerInventory<TileEntityBigButton> {

	public ContainerBigButton(IInventory playerInventory,
			TileEntityBigButton button) {
		super(playerInventory, button);
		addInventoryGrid(80, 23, 1);
		addPlayerInventorySlots(60);
	}
}
