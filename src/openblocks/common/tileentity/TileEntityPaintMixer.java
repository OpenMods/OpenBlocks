package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiPaintMixer;
import openblocks.common.GenericInventory;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.container.ContainerPaintMixer;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;

public class TileEntityPaintMixer extends SyncedTileEntity implements IInventory, IHasGui, IActivateAwareTile {
	
	public static final ItemStack PAINT_CAN = new ItemStack(OpenBlocks.Blocks.paintCan);
	
	public static enum Slots {
		input,
		dyeCyan,
		dyeMagenta,
		dyeYellow
	}
	
	private GenericInventory inventory = new GenericInventory("paintmixer", true, 4);
	private SyncableInt color;
	
	@Override
	protected void createSyncedFields() {
		color = new SyncableInt(0xFF0000);
	}
	
	public SyncableInt getColor() {
		return color;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player);
		}
		return true;
	}

    public void onSync() {
    	setPaintCanColor();
    }
	
	private void setPaintCanColor() {
		ItemStack inputStack = inventory.getStackInSlot(Slots.input);
		if (inputStack != null && inputStack.isItemEqual(PAINT_CAN)) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("color", color.getValue());
			inputStack.setTagCompound(tag);
		}
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
    	setPaintCanColor();
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerPaintMixer(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiPaintMixer(new ContainerPaintMixer(player.inventory, this));
	}


}
