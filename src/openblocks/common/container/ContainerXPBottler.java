package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.tileentity.TileEntityXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler.AutoSides;
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

		// autoflags contains flags to say is a particular slot should
		// autoeject/insert
		SyncableFlags autoFlags = xpBottler.getAutoFlags();

		if (buttonId < 7) {
			xpBottler.getGlassSides().toggle(ForgeDirection.getOrientation(buttonId));
		} else if (buttonId < 14) {
			xpBottler.getXPBottleSides().toggle(ForgeDirection.getOrientation(buttonId - 7));
		} else if (buttonId < 21) {
			xpBottler.getXPSides().toggle(ForgeDirection.getOrientation(buttonId - 14));
		} else if (buttonId == 21) {
			autoFlags.toggle(AutoSides.input);
		} else if (buttonId == 22) {
			autoFlags.toggle(AutoSides.output);
		} else if (buttonId == 23) {
			autoFlags.toggle(AutoSides.xp);
		}

	}

}
