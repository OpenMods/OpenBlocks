package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.tileentity.TileEntityXPBottler;
import openblocks.sync.SyncableFlags;

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

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		TileEntityXPBottler xpBottler = getTileEntity();
		xpBottler.updateGuiValues();
		xpBottler.sync(false);
	}

	@Override
	public void onClientButtonClicked(int buttonId) {
		
		TileEntityXPBottler xpBottler = getTileEntity();
		
		// autoflags contains flags to say is a particular slot should autoeject/insert
		SyncableFlags autoFlags = xpBottler.getAutoFlags();
		
		if (buttonId == 15) {
			autoFlags.toggle(TileEntityXPBottler.AutoSides.input);
		}else if (buttonId == 16) {
			autoFlags.toggle(TileEntityXPBottler.AutoSides.output);
		}else if (buttonId < 7) {
			xpBottler.getGlassSides().toggle(ForgeDirection.getOrientation(buttonId));
		} else {
			xpBottler.getXPSides().toggle(ForgeDirection.getOrientation(buttonId - 7));
		}
		
	}

}
