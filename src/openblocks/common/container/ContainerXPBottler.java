package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.tileentity.TileEntityXPBottler;

public class ContainerXPBottler extends ContainerInventory<TileEntityXPBottler> {

	public ContainerXPBottler(IInventory playerInventory, TileEntityXPBottler xpbottler) {
		super(playerInventory, xpbottler);
		// addInventoryGrid(80, 23, 2);
		addSlotToContainer(new RestrictedSlot(getTileEntity(), 0, 48, 30));
		addSlotToContainer(new RestrictedSlot(getTileEntity(), 1, 110, 30));
		addPlayerInventorySlots(69);
		xpbottler.sync();
	}

	@Override
	public void onServerButtonClicked(EntityPlayer player, int buttonId) {
		onClientButtonClicked(buttonId);
	}

	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		getTileEntity().sync(false);
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
