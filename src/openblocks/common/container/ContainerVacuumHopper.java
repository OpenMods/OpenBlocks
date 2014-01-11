package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.container.ContainerInventoryProvider;

public class ContainerVacuumHopper extends ContainerInventoryProvider<TileEntityVacuumHopper> {

	public ContainerVacuumHopper(IInventory playerInventory, TileEntityVacuumHopper hopper) {
		super(playerInventory, hopper);
		addInventoryGrid(44, 20, 5);
		addPlayerInventorySlots(69);
		if (!hopper.worldObj.isRemote) {
			hopper.sync();
		}
	}

}
