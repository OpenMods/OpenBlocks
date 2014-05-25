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
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.api.INeighbourAwareTile;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fakeplayer.FakePlayerPool.PlayerUser;
import openmods.fakeplayer.OpenModsFakePlayer;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.InventoryUtils;

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

		final ForgeDirection direction = getRotation();
		final int x = xCoord + direction.offsetX;
		final int y = yCoord + direction.offsetY;
		final int z = zCoord + direction.offsetZ;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			final int slotId = i;
			final ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null || stack.stackSize == 0) continue;
			placeBlock(direction, x, y, z, slotId, stack);
		}
	}

	private void placeBlock(final ForgeDirection direction, final int x, final int y, final int z, final int slotId, final ItemStack stack) {
		if (!(worldObj instanceof WorldServer)) return;
		FakePlayerPool.instance.executeOnPlayer((WorldServer)worldObj, new PlayerUser() {
			@Override
			public void usePlayer(OpenModsFakePlayer fakePlayer) {
				boolean blockExists;

				if (worldObj.blockExists(x, y, z)) {
					Block block = worldObj.getBlock(x, y, z);
					blockExists = !block.isAir(worldObj, x, y, z) && !block.isReplaceable(worldObj, x, y, z);
				} else blockExists = false;

				ItemStack newStack = fakePlayer.equipWithAndRightClick(stack,
						Vec3.createVectorHelper(xCoord, yCoord, zCoord),
						Vec3.createVectorHelper(x, y - 1, z),
						direction.getOpposite(),
						blockExists);
				inventory.setInventorySlotContents(slotId, newStack);
			}
		});
	}

	@Override
	public void onNeighbourChanged() {
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
