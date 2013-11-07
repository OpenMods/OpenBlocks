package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import openblocks.OpenBlocks;
import openblocks.client.gui.GuiClayStainer;
import openblocks.common.GenericInventory;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.block.BlockSpecialStainedClay;
import openblocks.common.container.ContainerClayStainer;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;

public class TileEntityClayStainer extends SyncedTileEntity implements IInventory, IHasGui, IActivateAwareTile {

	public static final ItemStack HARDENED_CLAY = new ItemStack(Block.hardenedClay);
	public static final ItemStack STAINED_CLAY = new ItemStack(Block.stainedClay);
	public static final ItemStack SPECIAL_STAINED_CLAY = new ItemStack(OpenBlocks.Blocks.specialStainedClay);
	
	public static enum Slots {
		input,
		dyeRed,
		dyeGreen,
		dyeBlue
	}
	
	private GenericInventory inventory = new GenericInventory("claystainer", true, 4);
	
	private SyncableInt color;
	private SyncableInt tone;
	
	@Override
	protected void createSyncedFields() {
		color = new SyncableInt();
		tone = new SyncableInt();
	}
	
	public SyncableInt getColor() {
		return color;
	}

	public SyncableInt getTone() {
		return tone;
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
		setColorOnItemStack();
	}
	
	public void setColorOnItemStack() {
		ItemStack inputStack = inventory.getStackInSlot(Slots.input);
		if (inputStack != null && inputStack.isItemEqual(SPECIAL_STAINED_CLAY)) {
			BlockSpecialStainedClay.writeColorToNBT(inputStack, color.getValue());
		}
	}
	
	@Override
	public void onSynced(List<ISyncableObject> changes) {
		setColorOnItemStack();
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
		setColorOnItemStack();
		if (itemstack != null) {

			Item item = itemstack.getItem();
			if (item != null && item.equals(HARDENED_CLAY.getItem()) || item.equals(STAINED_CLAY.getItem())) {
				int size = itemstack.stackSize;
				itemstack = SPECIAL_STAINED_CLAY.copy();
				itemstack.stackSize = size;
			}
		}
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
		return new ContainerClayStainer(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiClayStainer(new ContainerClayStainer(player.inventory, this));
	}


}
