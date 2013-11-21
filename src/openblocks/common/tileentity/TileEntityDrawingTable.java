package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openblocks.OpenBlocks.Items;
import openblocks.client.gui.GuiDrawingTable;
import openblocks.common.GenericInventory;
import openblocks.common.Stencil;
import openblocks.common.container.ContainerDrawingTable;
import openblocks.common.events.StencilCraftEvent;
import openblocks.common.events.TileEntityMessageEventPacket;
import openblocks.common.item.ItemGeneric;
import openmods.common.api.IActivateAwareTile;
import openmods.common.api.IHasGui;
import openmods.common.api.IInventoryCallback;

public class TileEntityDrawingTable extends OpenTileEntity implements
IInventory, IActivateAwareTile, IHasGui, IInventoryCallback {

	
	public TileEntityDrawingTable() {
		setInventory(new GenericInventory("drawingtable", true, 1));
		inventory.addCallback(this);
	}
	
	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {
		
	}

	public void onRequestStencilCreate(Stencil stencil) {
		new StencilCraftEvent(this, stencil).sendToServer();
	}

	@Override
	public void onEvent(TileEntityMessageEventPacket event) {
		if (event instanceof StencilCraftEvent) {
			ItemStack stack = inventory.getStackInSlot(0);
			if(stack != null && ItemGeneric.isA(stack, ItemGeneric.Metas.unpreparedStencil)) {
				ItemStack stencil = new ItemStack(Items.stencil, 1, ((StencilCraftEvent)event).getStencil().ordinal());
				stencil.stackSize = stack.stackSize;
				inventory.setInventorySlotContents(0, stencil);
			}
		}
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerDrawingTable(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiDrawingTable(new ContainerDrawingTable(player.inventory, this));
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX,
			float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player);
		}
		return true;
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
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}

}
