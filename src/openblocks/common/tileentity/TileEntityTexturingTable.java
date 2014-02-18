package openblocks.common.tileentity;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openblocks.client.gui.GuiSprinkler;
import openblocks.client.gui.GuiTexturingTable;
import openblocks.common.container.ContainerSprinkler;
import openblocks.common.container.ContainerTexturingTable;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableIntArray;
import openmods.tileentity.OpenTileEntity;

public class TileEntityTexturingTable extends OpenTileEntity implements IHasGui, IInventoryProvider {

	private SyncableInt clientColor;
	private SyncableIntArray clientColorGrid;
	
	public void initialize() {
		if (worldObj.isRemote)  {
			clientColor = new SyncableInt();
			int[] pixels = new int[16 * 16];
			Arrays.fill(pixels, 0xFFFFFFFF);
			clientColorGrid = new SyncableIntArray(pixels);
		}
	}
	
	private final GenericInventory inventory = new GenericInventory("texturingtable", true, 1) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return true;
		}
	};
	
	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerTexturingTable(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiTexturingTable(new ContainerTexturingTable(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	public SyncableInt getClientColor() {
		return clientColor;
	}

	public SyncableIntArray getClientColorGrid() {
		return clientColorGrid;
	}

}
