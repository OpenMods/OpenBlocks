package openblocks.common.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiDrawingTable;
import openblocks.common.StencilPattern;
import openblocks.common.container.ContainerDrawingTable;
import openblocks.common.item.ItemStencil;
import openblocks.common.item.MetasGeneric;
import openblocks.rpc.IStencilCrafter;
import openmods.api.ICustomBreakDrops;
import openmods.api.IHasGui;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncMap;
import openmods.sync.SyncableEnum;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityDrawingTable extends SyncedTileEntity implements IHasGui, IInventoryProvider, IStencilCrafter, ICustomBreakDrops {

	public static final int SLOT_INPUT = 0;

	public static final int SLOT_OUTPUT = 1;

	private SyncableEnum<StencilPattern> selectedPattern;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "drawingtable", true, 2) {
		@Override
		public boolean isItemValidForSlot(int slotId, ItemStack itemstack) {
			return itemstack.isEmpty() || (slotId == SLOT_INPUT && MetasGeneric.unpreparedStencil.isA(itemstack));
		}

		@Override
		public void onInventoryChanged(int slotNumber) {
			if (slotNumber == SLOT_INPUT) {
				final ItemStack input = inventoryContents.get(SLOT_INPUT);
				if (MetasGeneric.unpreparedStencil.isA(input)) {
					final ItemStack output = new ItemStack(OpenBlocks.Items.stencil, input.getCount(), selectedPattern.get().ordinal());
					inventoryContents.set(SLOT_OUTPUT, output);
				} else {
					inventoryContents.set(SLOT_OUTPUT, ItemStack.EMPTY);
				}
			} else if (slotNumber == SLOT_OUTPUT) {
				final ItemStack output = inventoryContents.get(SLOT_OUTPUT);
				if (output.getItem() instanceof ItemStencil) {
					final ItemStack input = MetasGeneric.unpreparedStencil.newItemStack(output.getCount());
					inventoryContents.set(SLOT_INPUT, input);
				} else {
					inventoryContents.set(SLOT_INPUT, ItemStack.EMPTY);
				}
			}

			super.onInventoryChanged(slotNumber);
		}
	});

	public TileEntityDrawingTable() {}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addSyncListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				if (changes.contains(selectedPattern))
					inventory.onInventoryChanged(SLOT_INPUT);
			}
		});
	}

	@Override
	protected void createSyncedFields() {
		selectedPattern = SyncableEnum.create(StencilPattern.CREEPER_FACE);
	}

	@Override
	public void selectionUp() {
		selectedPattern.increment();
		sync();
	}

	@Override
	public void selectionDown() {
		selectedPattern.decrement();
		sync();
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
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	@Override
	public List<ItemStack> getDrops(List<ItemStack> originalDrops) {
		List<ItemStack> drops = Lists.newArrayList();
		drops.add(inventory.getStackInSlot(SLOT_INPUT));
		return drops; // original drops ignored
	}

}
