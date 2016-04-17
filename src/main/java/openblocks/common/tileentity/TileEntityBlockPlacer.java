package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
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

	private void placeBlock(int slotId) {
		if (!(worldObj instanceof WorldServer)) return;

		final ItemStack stack = inventory.getStackInSlot(slotId);
		if (stack == null || stack.stackSize <= 0) return;

		final ForgeDirection direction = getOrientation().up();
		final int targetX = xCoord + direction.offsetX;
		final int targetY = yCoord + direction.offsetY;
		final int targetZ = zCoord + direction.offsetZ;

		if (worldObj.blockExists(targetX, targetY, targetZ)) {
			final Block block = worldObj.getBlock(targetX, targetY, targetZ);
			if (!block.isAir(worldObj, targetX, targetY, targetZ)
					&& !block.isReplaceable(worldObj, targetX, targetY, targetZ)) return;
		}

		// this logic is tuned for vanilla blocks (like pistons), which places blocks with front facing player
		// so to place object pointing in the same direction as placer, we need configuration player-target-placer
		// * 2, since some blocks may take into account player height, so distance must be greater than that
		final int playerX = targetX + 2 * direction.offsetX;
		final int playerY = targetY + 2 * direction.offsetY;
		final int playerZ = targetZ + 2 * direction.offsetZ;

		final ItemStack result = FakePlayerPool.instance.executeOnPlayer((WorldServer)worldObj, new UseItemAction(
				stack,
				Vec3.createVectorHelper(playerX, playerY, playerZ),
				Vec3.createVectorHelper(targetX, targetY, targetZ),
				Vec3.createVectorHelper(targetX + 0.5, targetY + 0.5, targetZ + 0.5),
				direction.getOpposite()
				));

		inventory.setInventorySlotContents(slotId, result);
	}

	@Override
	public void onNeighbourChanged(Block block) {
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
