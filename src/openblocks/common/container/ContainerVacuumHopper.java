package openblocks.common.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;

public class ContainerVacuumHopper extends ContainerInventory<TileEntityVacuumHopper> {

	private SyncableInt xpBufferAmount = new SyncableInt();
	private SyncableInt xpBufferCapacity = new SyncableInt();
	
	public enum Keys {
		xpBufferAmount,
		xpBufferCapacity
	}
	
	public ContainerVacuumHopper(IInventory playerInventory, TileEntityVacuumHopper hopper) {
		super(playerInventory, hopper);
		addInventoryGrid(44, 20, 5);
		addPlayerInventorySlots(69);
		addSyncedObject(Keys.xpBufferAmount, xpBufferAmount);
		addSyncedObject(Keys.xpBufferCapacity, xpBufferCapacity);
		hopper.sync();
	}

	public double getXPBufferRatio() {
		return (double) xpBufferAmount.getValue() / (double) xpBufferCapacity.getValue();
	}
	
	@Override
	public void detectAndSendChanges() {
		// sync the fluid amount and capacity via the container
		// so that it only sends updates to people looking at the gui
		xpBufferAmount.setValue(getTileEntity().getTank().getFluidAmount());
		xpBufferCapacity.setValue(getTileEntity().getTank().getCapacity());
		super.detectAndSendChanges();
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
		if (buttonId < 7) {
			getTileEntity().getXPOutputs().toggle(ForgeDirection.getOrientation(buttonId));
		} else {
			getTileEntity().getItemOutputs().toggle(ForgeDirection.getOrientation(buttonId - 7));
		}
	}

}
