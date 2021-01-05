package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.container.ContainerInventoryProvider;

public class ContainerVacuumHopper extends ContainerInventoryProvider<TileEntityVacuumHopper> {
	public ContainerVacuumHopper(IInventory playerInventory, int id, TileEntityVacuumHopper hopper) {
		super(OpenBlocks.Containers.vacuumHopper, id, playerInventory, hopper);
		addInventoryGrid(44, 20, 5);
		addPlayerInventorySlots(69);
	}
}
