package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityDonationStation;

public class ContainerDonationStation extends ContainerInventory<TileEntityDonationStation> {

	public ContainerDonationStation(IInventory playerInventory, TileEntityDonationStation station) {
		super(playerInventory, station);
		addInventoryGrid(44, 27, 1);
		addPlayerInventorySlots(70);
	}

}
