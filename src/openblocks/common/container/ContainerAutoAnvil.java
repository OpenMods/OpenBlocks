package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openblocks.sync.SyncableFlags;

public class ContainerAutoAnvil extends ContainerInventory<TileEntityAutoAnvil> {

	public ContainerAutoAnvil(IInventory playerInventory, TileEntityAutoAnvil tile) {
		super(playerInventory, tile);
		addSlotToContainer(new RestrictedSlot(tile, 0, 14, 40));
		addSlotToContainer(new RestrictedSlot(tile, 1, 56, 40));
		addSlotToContainer(new RestrictedSlot(tile, 2, 110, 40));
		addPlayerInventorySlots(93);
		tile.sync();
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		TileEntityAutoAnvil anvil = getTileEntity();
		anvil.updateGuiValues();
		anvil.sync(false);
	}

	@Override
	public void onServerButtonClicked(EntityPlayer player, int buttonId) {
		onClientButtonClicked(buttonId);
	}

	@Override
	public void onClientButtonClicked(int buttonId) {
		TileEntityAutoAnvil anvil = getTileEntity();

		// autoFlags is a set of flags to say if different sides should auto
		// inject/eject
		SyncableFlags autoFlags = anvil.getAutoFlags();

		if (buttonId < 7) {
			anvil.getToolSides().toggle(buttonId);
		} else if (buttonId < 14) {
			anvil.getModifierSides().toggle(buttonId - 7);
		} else if (buttonId < 21) {
			anvil.getOutputSides().toggle(buttonId - 14);
		} else if (buttonId < 28) {
			anvil.getXPSides().toggle(buttonId - 21);

		} else if (buttonId == 28) {
			autoFlags.toggle(TileEntityAutoAnvil.AutoSides.tool);
		} else if (buttonId == 29) {
			autoFlags.toggle(TileEntityAutoAnvil.AutoSides.modifier);
		} else if (buttonId == 30) {
			autoFlags.toggle(TileEntityAutoAnvil.AutoSides.output);
		} else if (buttonId == 31) {
			autoFlags.toggle(TileEntityAutoAnvil.AutoSides.xp);
		}
	}

}
