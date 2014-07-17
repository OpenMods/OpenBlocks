package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openblocks.OpenBlocks.Items;
import openblocks.client.gui.GuiDrawingTable;
import openblocks.common.Stencil;
import openblocks.common.container.ContainerDrawingTable;
import openblocks.common.item.MetasGeneric;
import openblocks.rpc.IStencilCrafter;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.api.IInventoryCallback;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.tileentity.OpenTileEntity;

public class TileEntityDrawingTable extends OpenTileEntity implements IHasGui, IInventoryCallback, IExtendable, IInventoryProvider, IStencilCrafter {

	private final GenericInventory inventory = new GenericInventory("drawingtable", true, 1) {

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return i == 0 && (itemstack == null || MetasGeneric.unpreparedStencil.isA(itemstack));
		}

	};

	public TileEntityDrawingTable() {
		inventory.addCallback(this);
	}

	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerDrawingTable(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiDrawingTable(new ContainerDrawingTable(player.inventory, this));
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

	@Override
	public void craft(Stencil stencil) {
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack != null && MetasGeneric.unpreparedStencil.isA(stack)) {
			ItemStack stencilItem = new ItemStack(Items.stencil, 1, stencil.ordinal());
			stencilItem.stackSize = stack.stackSize;
			inventory.setInventorySlotContents(0, stencilItem);
		}
	}
}
