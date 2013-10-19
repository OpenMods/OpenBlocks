package openblocks.common.tileentity;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.GenericInventory;
import openblocks.common.api.IAwareTile;
import openblocks.utils.BlockUtils;
import openblocks.utils.InventoryUtils;

public class TileEntityBlockBreaker extends OpenTileEntity
		implements IAwareTile, IInventory {

	public enum Slots {
		buffer
	}

	private boolean _redstoneSignal;

	private GenericInventory fakeInventory = new GenericInventory("blockbreaker", true, 1);

	public void setRedstoneSignal(boolean redstoneSignal) {
		if (redstoneSignal != _redstoneSignal) {
			_redstoneSignal = redstoneSignal;
			if (_redstoneSignal) {
				breakBlock();
			}
		}
	}

	private void breakBlock() {
		if (worldObj.isRemote) return;

		ForgeDirection direction = get3dRotation();
		int x = xCoord + direction.offsetX, y = yCoord + direction.offsetY, z = zCoord
				+ direction.offsetZ;

		if (worldObj.blockExists(x, y, z)) {
			int blockId = worldObj.getBlockId(x, y, z);
			if (blockId > 0) {
				Block block = Block.blocksList[blockId];

				int metadata = worldObj.getBlockMetadata(x, y, z);
				worldObj.playAuxSFX(2001, x, y, z, blockId + (metadata << 12));

				ArrayList<ItemStack> items = block.getBlockDropped(worldObj, x, y, z, metadata, 0);

				worldObj.setBlock(x, y, z, 0, 0, 3);

				ForgeDirection back = direction.getOpposite();
				ejectAt(worldObj,
						xCoord + back.offsetX,
						yCoord + back.offsetY,
						zCoord + back.offsetZ,
						back, items);
			}
		}
	}

	public void ejectAt(World world, int x, int y, int z, ForgeDirection direction, ArrayList<ItemStack> itemStacks) {

		TileEntity targetInventory = getTileInDirection(direction);
		for (ItemStack stack : itemStacks) {
			// if there's any stack in our buffer slot, eject it. Why is it
			// there?
			ItemStack currentStack = fakeInventory.getStackInSlot(Slots.buffer);
			if (currentStack != null) {
				BlockUtils.ejectItemInDirection(world, x, y, z, direction, currentStack);
			}

			// clear the buffer slot
			fakeInventory.setInventorySlotContents(Slots.buffer.ordinal(), stack);

			// push the item out into a pipe or inventory
			InventoryUtils.moveItemInto(this, Slots.buffer.ordinal(), targetInventory, -1, 64, direction, true);

			// if there's anything left for whatever reason (maybe no inventory)
			ItemStack buffer = fakeInventory.getStackInSlot(Slots.buffer);
			if (buffer != null) {
				// eject it
				BlockUtils.ejectItemInDirection(world, x, y, z, direction, buffer);
			}
		}
	}

	static void ejectItemsAt(World world, int x, int y, int z, ForgeDirection direction, ArrayList<ItemStack> itemStacks) {
		if (!world.isRemote
				&& world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
		{
			for (int i = 0, l = itemStacks.size(); i < l; i++) {
				BlockUtils.ejectItemInDirection(world, x, y, z, direction, itemStacks.get(i));
			}
		}
	}

	@Override
	public void onBlockBroken() {}

	@Override
	public void onBlockAdded() {}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		if (!worldObj.isRemote) {
			setRedstoneSignal(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
		}
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		set3dRotation(BlockUtils.get3dOrientation(player));
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return fakeInventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return fakeInventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return fakeInventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return fakeInventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		fakeInventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return fakeInventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return fakeInventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return fakeInventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return fakeInventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

}
