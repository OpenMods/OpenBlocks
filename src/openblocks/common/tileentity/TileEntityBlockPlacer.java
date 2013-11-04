package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.client.gui.GuiBlockPlacer;
import openblocks.common.GenericInventory;
import openblocks.common.api.INeighbourAwareTile;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.container.ContainerBlockPlacer;
import openblocks.utils.InventoryUtils;
import openblocks.utils.OpenBlocksFakePlayer;

public class TileEntityBlockPlacer extends OpenTileEntity
		implements INeighbourAwareTile, IActivateAwareTile, IInventory, IHasGui {

	static final int BUFFER_SIZE = 9;

	private boolean _redstoneSignal;

	public TileEntityBlockPlacer() {
		setInventory(new GenericInventory("blockPlacer", false, BUFFER_SIZE));
	}
	
	public void setRedstoneSignal(boolean redstoneSignal) {
		if (redstoneSignal != _redstoneSignal) {
			_redstoneSignal = redstoneSignal;
			if (_redstoneSignal && !InventoryUtils.inventoryIsEmpty(inventory)) {
				placeBlock();
			}
		}
	}

	private void placeBlock() {
		if (worldObj.isRemote) return;

		ForgeDirection direction = getRotation();
		int x = xCoord + direction.offsetX, y = yCoord + direction.offsetY, z = zCoord + direction.offsetZ;
		for (int i = 0, l = getSizeInventory(); i < l; i++) {
			ItemStack stack = getStackInSlot(i);
			if (stack == null || stack.stackSize == 0) continue;
			ItemStack newStack = OpenBlocksFakePlayer.getPlayerForWorld(worldObj)
					.equipWithAndRightClick(stack,
							Vec3.createVectorHelper(xCoord, yCoord, zCoord),
							Vec3.createVectorHelper(x, y - 1, z),
							direction.getOpposite(),
							worldObj.blockExists(x, y, z) && !worldObj.isAirBlock(x, y, z) && !Block.blocksList[worldObj.getBlockId(x, y, z)].isBlockReplaceable(worldObj, x, y, z));

			setInventorySlotContents(i, newStack.stackSize > 0? newStack : null);
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
		return new ContainerBlockPlacer(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiBlockPlacer(new ContainerBlockPlacer(player.inventory, this));
	}
}
