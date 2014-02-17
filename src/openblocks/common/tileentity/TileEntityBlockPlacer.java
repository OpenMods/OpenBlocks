package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.client.gui.GuiBlockPlacer;
import openblocks.common.container.ContainerBlockPlacer;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.api.INeighbourAwareTile;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.InventoryUtils;
import openmods.utils.OpenModsFakePlayer;

public class TileEntityBlockPlacer extends OpenTileEntity implements INeighbourAwareTile, IHasGui, IExtendable, IInventoryProvider {

	static final int BUFFER_SIZE = 9;

	private boolean _redstoneSignal;

	private final GenericInventory inventory = new GenericInventory("blockPlacer", false, BUFFER_SIZE);

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
		final int x = xCoord + direction.offsetX;
		final int y = yCoord + direction.offsetY;
		final int z = zCoord + direction.offsetZ;

		for (int i = 0, l = inventory.getSizeInventory(); i < l; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null || stack.stackSize == 0) continue;
			OpenModsFakePlayer fakePlayer = OpenModsFakePlayer.getPlayerForWorld(worldObj);
			ItemStack newStack = OpenModsFakePlayer.getPlayerForWorld(worldObj)
					.equipWithAndRightClick(stack,
							Vec3.createVectorHelper(xCoord, yCoord, zCoord),
							Vec3.createVectorHelper(x, y - 1, z),
							direction.getOpposite(),
							worldObj.blockExists(x, y, z) && !worldObj.isAirBlock(x, y, z) && !Block.blocksList[worldObj.getBlockId(x, y, z)].isBlockReplaceable(worldObj, x, y, z));
			fakePlayer.setDead();
			inventory.setInventorySlotContents(i, newStack);
			break;
		}
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

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	@Override
	@IncludeInterface
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}
}
