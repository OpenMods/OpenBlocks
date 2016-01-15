package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import openblocks.client.gui.GuiBlockPlacer;
import openblocks.common.container.ContainerBlockPlacer;
import openmods.api.IHasGui;
import openmods.api.INeighbourAwareTile;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fakeplayer.UseItemAction;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.InventoryUtils;

public class TileEntityBlockPlacer extends OpenTileEntity implements INeighbourAwareTile, IHasGui, IInventoryProvider {

	static final int BUFFER_SIZE = 9;

	private static final int EVENT_ACTIVATE = 2;

	private boolean _redstoneSignal;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "blockPlacer", false, BUFFER_SIZE));

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

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			final ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.stackSize > 0) {
				sendBlockEvent(EVENT_ACTIVATE, i);
				break;
			}
		}
	}

	@Override
	public boolean receiveClientEvent(int event, int param) {
		if (event == EVENT_ACTIVATE) {
			placeBlock(param);
			return true;
		}

		return false;
	}

	private void placeBlock(final int slotId) {
		if (!(worldObj instanceof WorldServer)) return;

		final ItemStack stack = inventory.getStackInSlot(slotId);
		if (stack == null || stack.stackSize <= 0) return;

		final EnumFacing direction = getOrientation().up();
		final BlockPos target = pos.offset(direction);

		if (!worldObj.isBlockLoaded(target)) return;

		final Block block = worldObj.getBlockState(target).getBlock();
		if (!block.isAir(worldObj, target) && !block.isReplaceable(worldObj, target)) return;

		// TODO 1.8.9 verify placing
		ItemStack newStack = FakePlayerPool.instance.executeOnPlayer((WorldServer)worldObj, new UseItemAction(stack,
				new Vec3(pos),
				new Vec3(pos.down()),
				direction.getOpposite()));

		inventory.setInventorySlotContents(slotId, newStack);
	}

	@Override
	public void onNeighbourChanged(Block block) {
		if (!worldObj.isRemote) {
			setRedstoneSignal(worldObj.isBlockIndirectlyGettingPowered(pos) > 0);
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
