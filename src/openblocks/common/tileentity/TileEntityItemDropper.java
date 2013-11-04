package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openblocks.client.gui.GuiItemDropper;
import openblocks.common.GenericInventory;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.api.INeighbourAwareTile;
import openblocks.common.container.ContainerItemDropper;
import openblocks.utils.InventoryUtils;
import openblocks.utils.OpenBlocksFakePlayer;

public class TileEntityItemDropper extends OpenTileEntity
		implements IActivateAwareTile, INeighbourAwareTile, IInventory, IHasGui {
	static final int BUFFER_SIZE = 9;

	private boolean _redstoneSignal;
	
	public TileEntityItemDropper() {
		setInventory(new GenericInventory("itemDropper", false, 9));
	}

	public void setRedstoneSignal(boolean redstoneSignal) {
		if (redstoneSignal != _redstoneSignal) {
			_redstoneSignal = redstoneSignal;
			if (_redstoneSignal && !InventoryUtils.inventoryIsEmpty(inventory)) {
				dropItem();
			}
		}
	}

	private void dropItem() {
		if (worldObj.isRemote) return;

		for (int i = 0, l = getSizeInventory(); i < l; i++) {
			ItemStack stack = getStackInSlot(i);
			if (stack == null || stack.stackSize == 0) continue;

			ItemStack dropped = stack.splitStack(1);
			if (stack.stackSize <= 0) {
				setInventorySlotContents(i, null);
			}

			OpenBlocksFakePlayer.getPlayerForWorld(worldObj).dropItemAt(dropped, xCoord, yCoord, zCoord, ForgeDirection.DOWN);

			return;
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		if (!worldObj.isRemote) {
			setRedstoneSignal(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
		}
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerItemDropper(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiItemDropper(new ContainerItemDropper(player.inventory, this));
	}

}
