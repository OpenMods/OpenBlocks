package openblocks.common.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.sync.ISyncableObject;

public class ContainerLightbox extends ContainerInventory<TileEntityLightbox> {

	public ContainerLightbox(IInventory playerInventory, TileEntityLightbox lightbox) {
		super(playerInventory, lightbox);
		addInventoryGrid(80, 23, 1);
		addPlayerInventorySlots(60);
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		// TODO Auto-generated method stub

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
