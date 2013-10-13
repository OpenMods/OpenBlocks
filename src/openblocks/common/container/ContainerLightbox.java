package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityLightbox;

public class ContainerLightbox extends ContainerInventory<TileEntityLightbox> {

	public ContainerLightbox(IInventory playerInventory, TileEntityLightbox lightbox) {
		super(playerInventory, lightbox);
		addInventoryGrid(80, 23, 1);
		addPlayerInventorySlots(60);
	}

	@Override
	public void onServerButtonClicked(EntityPlayer player, int buttonId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClientButtonClicked(int buttonId) {
		// TODO Auto-generated method stub

	}

}
