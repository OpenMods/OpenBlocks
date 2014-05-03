package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityDigitalFuse;
import openmods.GenericInventory;
import openmods.container.ContainerBase;

public class ContainerDigitalFuse extends ContainerBase<TileEntityDigitalFuse> {

	public ContainerDigitalFuse(IInventory playerInventory, TileEntityDigitalFuse owner) {
		super(playerInventory, new GenericInventory("", false, 0), owner);
		addPlayerInventorySlots(95);
	}

}
