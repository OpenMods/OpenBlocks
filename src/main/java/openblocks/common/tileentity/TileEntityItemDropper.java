package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.client.gui.GuiItemDropper;
import openblocks.common.container.ContainerItemDropper;
import openmods.api.IHasGui;
import openmods.api.INeighbourAwareTile;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fakeplayer.FakePlayerPool.PlayerUser;
import openmods.fakeplayer.OpenModsFakePlayer;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.InventoryUtils;

public class TileEntityItemDropper extends OpenTileEntity implements INeighbourAwareTile, IInventoryProvider, IHasGui {
	static final int BUFFER_SIZE = 9;

	private boolean _redstoneSignal;

	private GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "itemDropper", false, 9));

	public TileEntityItemDropper() {}

	public void setRedstoneSignal(boolean redstoneSignal) {
		if (redstoneSignal != _redstoneSignal) {
			_redstoneSignal = redstoneSignal;
			if (_redstoneSignal && !InventoryUtils.inventoryIsEmpty(inventory)) {
				dropItem();
			}
		}
	}

	private void dropItem() {
		if (!(worldObj instanceof WorldServer)) return;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null || stack.stackSize <= 0) continue;

			final ItemStack dropped = stack.splitStack(1);

			if (stack.stackSize <= 0) inventory.setInventorySlotContents(i, null);

			FakePlayerPool.instance.executeOnPlayer((WorldServer)worldObj, new PlayerUser() {
				@Override
				public void usePlayer(OpenModsFakePlayer fakePlayer) {
					fakePlayer.dropItemAt(dropped, xCoord, yCoord, zCoord, ForgeDirection.DOWN);
				}
			});

			break;
		}
	}

	@Override
	public void onNeighbourChanged(Block block) {
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
