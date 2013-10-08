package openblocks.common.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.container.ContainerInventory.RestrictedSlot;
import openblocks.common.tileentity.TileEntityXPBottler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;

public class ContainerXPBottler extends ContainerInventory<TileEntityXPBottler> {

	private SyncableInt xpBufferAmount = new SyncableInt();
	private SyncableInt xpBufferCapacity = new SyncableInt();
	
	public enum Keys {
		xpBufferAmount,
		xpBufferCapacity,
		progress
	}
	
	public ContainerXPBottler(IInventory playerInventory, TileEntityXPBottler xpbottler) {
		super(playerInventory, xpbottler);
		//addInventoryGrid(80, 23, 2);
		addSlotToContainer(new RestrictedSlot(getTileEntity(), 0, 48, 30));
		addSlotToContainer(new RestrictedSlot(getTileEntity(), 1, 110, 30));
		addPlayerInventorySlots(69);
		addSyncedObject(Keys.xpBufferAmount, xpBufferAmount);
		addSyncedObject(Keys.xpBufferCapacity, xpBufferCapacity);
		addSyncedObject(Keys.progress, xpbottler.getProgress());
		xpbottler.sync();
	}

	public double getXPBufferRatio() {
		return (double) xpBufferAmount.getValue() / (double) xpBufferCapacity.getValue();
	}
	
	@Override
	public void detectAndSendChanges() {
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
		sync();
	}

	@Override
	public void onClientButtonClicked(int buttonId) {
		if (buttonId < 7) {
			getTileEntity().getGlassSides().toggle(ForgeDirection.getOrientation(buttonId));
		} else {
			getTileEntity().getXPSides().toggle(ForgeDirection.getOrientation(buttonId - 7));
		}
	}

}
