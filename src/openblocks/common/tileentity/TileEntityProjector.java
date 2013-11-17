package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import openblocks.client.gui.GuiProjector;
import openblocks.common.GenericInventory;
import openblocks.common.HeightMapData;
import openblocks.common.MapDataManager;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.api.IInventoryCallback;
import openblocks.common.container.ContainerProjector;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemHeightMap;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableByte;
import openblocks.sync.SyncableInt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityProjector extends SyncedTileEntity implements IInventory, IActivateAwareTile, IHasGui {

	private GenericInventory inventory = new GenericInventory("openblocks.projector", false, 1) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack stack) {
			return stack == null || stack.getItem() instanceof ItemHeightMap;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public void onInventoryChanged(int slotNumber) {
			super.onInventoryChanged(slotNumber);

			if (!isInvalid()) {
				if (!worldObj.isRemote) {
					ItemStack stack = getStackInSlot(slotNumber);
					if (stack != null && stack.stackSize == 1) {
						Item item = stack.getItem();
						if (item instanceof ItemHeightMap) {
							int mapId = stack.getItemDamage();
							TileEntityProjector.this.mapId.setValue(mapId);
						} else if (item instanceof ItemEmptyMap && worldObj != null) {
							ItemStack newStack = ItemEmptyMap.upgradeToMap(worldObj, stack);
							setInventorySlotContents(slotNumber, newStack);
						} else TileEntityProjector.this.mapId.setValue(-1);
					} else
					TileEntityProjector.this.mapId.setValue(-1);
				}

				worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, TileEntityProjector.this);
			}
		}
	};

	private SyncableByte rotation;
	private SyncableInt mapId;

	public static class MapInventory extends GenericInventory {

		public MapInventory(String name, boolean isInvNameLocalized, int size) {
			super(name, isInvNameLocalized, size);
		}
	}

	public GenericInventory addUpdateCallback(MapInventory inventory, final TileEntity te) {
		inventory.addCallback(new IInventoryCallback() {
			@Override
			public void onInventoryChanged(IInventory inventory, int slotNumber) {

			}
		});

		return inventory;
	}

	@Override
	protected void createSyncedFields() {
		rotation = new SyncableByte();
		mapId = new SyncableInt(-1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1);
	}

	@Override
	public void validate() {
		super.validate();
		inventory.onInventoryChanged(0);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0 || pass == 1;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
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
	public ItemStack decrStackSize(int slot, int quantity) {
		return inventory.decrStackSize(slot, quantity);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		inventory.setInventorySlotContents(i, stack);
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
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return inventory.isItemValidForSlot(i, stack);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) openGui(player);
		return true;
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerProjector(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiProjector(new ContainerProjector(player.inventory, this));
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		if (worldObj.isRemote && changes.contains(mapId)) {
			int mapId = this.mapId.getValue();
			if (mapId >= 0 && MapDataManager.getMapData(worldObj, mapId).isEmpty()) MapDataManager.requestMapData(worldObj, mapId);
		}
	}

	public void rotate(int delta) {
		int value = rotation.getValue() + delta;
		rotation.setValue((byte)(value & 0x3));
		sync();
	}

	public byte rotation() {
		return rotation.getValue();
	}

	public int mapId() {
		return mapId.getValue();
	}

	public HeightMapData getMap() {
		int mapId = this.mapId.getValue();
		if (worldObj == null || mapId < 0) return null;

		return MapDataManager.getMapData(worldObj, mapId);
	}
	
	public void markMapDirty() {
		int mapId = this.mapId.getValue();
		if (worldObj != null || mapId < 0) MapDataManager.instance.markDataUpdated(worldObj, mapId);
	}

	public void fetchMap() {
		int mapId = this.mapId.getValue();
		if (worldObj != null && mapId >= 0) MapDataManager.getMapData(worldObj, mapId);
	}
}
