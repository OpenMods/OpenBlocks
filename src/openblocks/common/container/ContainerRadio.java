package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityRadio;
import openmods.container.ContainerInventoryProvider;

public class ContainerRadio extends ContainerInventoryProvider<TileEntityRadio> {

	public ContainerRadio(IInventory playerInventory, TileEntityRadio owner) {
		super(playerInventory, owner);
		addPlayerInventorySlots(69);
	}

}
