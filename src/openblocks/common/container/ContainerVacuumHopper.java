package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityVacuumHopper;

public class ContainerVacuumHopper extends ContainerInventory<TileEntityVacuumHopper> {

	public ContainerVacuumHopper(IInventory playerInventory, TileEntityVacuumHopper sprinkler) {
		super(playerInventory, sprinkler);
		addInventoryGrid(44, 20, 5);
		addPlayerInventorySlots(69);
	}

}
