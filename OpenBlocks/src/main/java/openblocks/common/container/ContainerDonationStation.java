package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityDonationStation;
import openmods.container.ContainerInventoryProvider;

public class ContainerDonationStation extends ContainerInventoryProvider<TileEntityDonationStation> {

	public ContainerDonationStation(IInventory playerInventory, TileEntityDonationStation station) {
		super(playerInventory, station);
		addInventoryGrid(30, 30, 1);
		addPlayerInventorySlots(90);
	}
}
