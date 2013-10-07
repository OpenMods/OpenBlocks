package openblocks.common.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.sync.ISyncableObject;

public class ContainerVacuumHopper extends ContainerInventory<TileEntityVacuumHopper> {

	public ContainerVacuumHopper(IInventory playerInventory, TileEntityVacuumHopper hopper) {
		super(playerInventory, hopper);
		addInventoryGrid(44, 20, 5);
		addPlayerInventorySlots(69);
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
	}

	@Override
	public void onServerButtonClicked(EntityPlayer player, int buttonId) {
		onClientButtonClicked(buttonId);
		getTileEntity().sync(false);
	}

	@Override
	public void onClientButtonClicked(int buttonId) {
		if (buttonId >= 0 && buttonId <= 7) {
			getTileEntity().getXPOutputs().toggleDirection(ForgeDirection.getOrientation(buttonId));
		} else {
			getTileEntity().getItemOutputs().toggleDirection(ForgeDirection.getOrientation(buttonId - 7));
		}
	}

}
