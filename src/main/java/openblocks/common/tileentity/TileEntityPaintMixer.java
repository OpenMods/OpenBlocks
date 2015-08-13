package openblocks.common.tileentity;

import java.util.EnumMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiPaintMixer;
import openblocks.common.container.ContainerPaintMixer;
import openblocks.common.item.ItemPaintCan;
import openblocks.rpc.IColorChanger;
import openmods.api.IHasGui;
import openmods.api.IInventoryCallback;
import openmods.api.IValueProvider;
import openmods.gamelogic.WorkerLogic;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableFloat;
import openmods.sync.SyncableInt;
import openmods.sync.drops.DroppableTileEntity;
import openmods.sync.drops.StoreOnDrop;
import openmods.utils.ColorUtils;
import openmods.utils.MiscUtils;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Maps;

public class TileEntityPaintMixer extends DroppableTileEntity implements IInventoryProvider, IHasGui, IInventoryCallback, IColorChanger {

	private static final ItemStack PAINT_CAN = new ItemStack(OpenBlocks.Blocks.paintCan);
	private static final ItemStack MILK_BUCKET = new ItemStack(Items.milk_bucket);
	public static final int PROGRESS_TICKS = 300;

	public static enum Slots {
		paint,
		reserved, // old output slot, now merged with input
		dyeCyan,
		dyeMagenta,
		dyeYellow,
		dyeBlack
	}

	public static enum DyeSlot {
		cyan,
		magenta,
		yellow,
		black
	}

	private static EnumMap<Slots, Integer> ALLOWED_COLORS = Maps.newEnumMap(Slots.class);

	static {
		ALLOWED_COLORS.put(Slots.dyeBlack, OreDictionary.getOreID("dyeBlack"));
		ALLOWED_COLORS.put(Slots.dyeCyan, OreDictionary.getOreID("dyeCyan"));
		ALLOWED_COLORS.put(Slots.dyeMagenta, OreDictionary.getOreID("dyeMagenta"));
		ALLOWED_COLORS.put(Slots.dyeYellow, OreDictionary.getOreID("dyeYellow"));
	}

	public enum Flags {
		hasPaint
	}

	private SyncableInt canColor;

	@StoreOnDrop
	private SyncableInt color;
	private SyncableInt progress;
	private SyncableFlags flags;
	private final WorkerLogic logic = new WorkerLogic(progress, PROGRESS_TICKS);

	// These could be optimized with a byte array later
	// Not important for release
	// Levels should be 0-2, so that if there is 0.3 left, 1 can be consumed and
	// not overflow ;)

	@StoreOnDrop
	public SyncableFloat lvlCyan;

	@StoreOnDrop
	public SyncableFloat lvlMagenta;

	@StoreOnDrop
	public SyncableFloat lvlYellow;

	@StoreOnDrop
	public SyncableFloat lvlBlack;

	private GenericInventory inventory = new TileEntityInventory(this, "paintmixer", true, 6) {
		@Override
		public boolean isItemValidForSlot(int slotId, ItemStack stack) {
			Slots[] values = Slots.values();
			if (stack == null || slotId < 0 || slotId > values.length) return false;
			Slots slot = values[slotId];

			if (slot == Slots.paint) return PAINT_CAN.isItemEqual(stack) || MILK_BUCKET.isItemEqual(stack);
			return isValidForSlot(slot, stack);
		}
	};

	public TileEntityPaintMixer() {
		inventory.addCallback(this);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

			if (logic.isWorking()) {
				if (!hasValidInput() || !hasSufficientInk()) {
					logic.reset();
				} else if (logic.update()) {
					consumeInk();
					ItemStack output = ItemPaintCan.createStack(color.get(), ItemPaintCan.FULL_CAN_SIZE);
					inventory.setInventorySlotContents(Slots.paint.ordinal(), output);
					canColor.set(color.get());
				}
			}

			checkAutoConsumption();
			sync();
		}
	}

	private void checkAutoConsumption() {
		if (lvlCyan.get() <= 1f) { /* We can store 2.0, so <= */
			if (tryUseInk(Slots.dyeCyan, 1)) {
				lvlCyan.set(lvlCyan.get() + 1f);
			}
		}
		if (lvlMagenta.get() <= 1f) {
			if (tryUseInk(Slots.dyeMagenta, 1)) {
				lvlMagenta.set(lvlMagenta.get() + 1f);
			}
		}
		if (lvlYellow.get() <= 1f) {
			if (tryUseInk(Slots.dyeYellow, 1)) {
				lvlYellow.set(lvlYellow.get() + 1f);
			}
		}
		if (lvlBlack.get() <= 1f) {
			if (tryUseInk(Slots.dyeBlack, 1)) {
				lvlBlack.set(lvlBlack.get() + 1f);
			}
		}

	}

	private void consumeInk() {
		ColorUtils.CYMK cymk = new ColorUtils.RGB(color.get()).toCYMK();
		lvlCyan.set(lvlCyan.get() - cymk.getCyan());
		lvlBlack.set(lvlBlack.get() - cymk.getKey());
		lvlYellow.set(lvlYellow.get() - cymk.getYellow());
		lvlMagenta.set(lvlMagenta.get() - cymk.getMagenta());
	}

	private boolean hasSufficientInk() {
		ColorUtils.CYMK cymk = new ColorUtils.RGB(color.get()).toCYMK();
		if (cymk.getCyan() > lvlCyan.get()) {
			if (tryUseInk(Slots.dyeCyan, 1)) {
				lvlCyan.set(lvlCyan.get() + 1f);
			} else {
				return false;
			}
		}
		if (cymk.getYellow() > lvlYellow.get()) {
			if (tryUseInk(Slots.dyeYellow, 1)) {
				lvlYellow.set(lvlYellow.get() + 1f);
			} else {
				return false;
			}
		}
		if (cymk.getMagenta() > lvlMagenta.get()) {
			if (tryUseInk(Slots.dyeMagenta, 1)) {
				lvlMagenta.set(lvlMagenta.get() + 1f);
			} else {
				return false;
			}
		}
		if (cymk.getKey() > lvlBlack.get()) {
			if (tryUseInk(Slots.dyeBlack, 1)) {
				lvlBlack.set(lvlBlack.get() + 1f);
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean tryUseInk(Slots slot, int consume) {
		ItemStack stack = inventory.getStackInSlot(slot);
		return isValidForSlot(slot, stack) && inventory.decrStackSize(slot.ordinal(), consume) != null;
	}

	private static boolean isValidForSlot(Slots slot, ItemStack stack) {
		Integer allowedColor = ALLOWED_COLORS.get(slot);
		if (allowedColor == null || stack == null) return false;
		int[] oreIds = OreDictionary.getOreIDs(stack);
		return ArrayUtils.contains(oreIds, allowedColor);
	}

	@Override
	protected void createSyncedFields() {
		color = new SyncableInt(0xFF0000);
		flags = SyncableFlags.create(Flags.values().length);
		progress = new SyncableInt();
		lvlBlack = new SyncableFloat();
		lvlCyan = new SyncableFloat();
		lvlMagenta = new SyncableFloat();
		lvlYellow = new SyncableFloat();
		canColor = new SyncableInt(0xFFFFFF);
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerPaintMixer(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiPaintMixer(new ContainerPaintMixer(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	@Override
	public void changeColor(int requestedColor) {
		if (!worldObj.isRemote) {
			if (logic.isWorking()) {
				if (requestedColor != color.get()) logic.reset();
				else return;
			}
			color.set(requestedColor);
			logic.start();
		}
	}

	public IValueProvider<Integer> getProgress() {
		return progress;
	}

	public IValueProvider<Integer> getColor() {
		return color;
	}

	public IValueProvider<Float> getDyeSlot(DyeSlot slot) {
		switch (slot) {
			case black:
				return lvlBlack;
			case cyan:
				return lvlCyan;
			case magenta:
				return lvlMagenta;
			case yellow:
				return lvlYellow;
			default:
				throw MiscUtils.unhandledEnum(slot);
		}
	}

	public boolean hasPaint() {
		return flags.get(Flags.hasPaint);
	}

	public int getCanColor() {
		return canColor.get();
	}

	public boolean isEnabled() {
		return progress.get() > 0;
	}

	public boolean hasValidInput() {
		return hasStack(Slots.paint, PAINT_CAN) || hasStack(Slots.paint, MILK_BUCKET);
	}

	private static Integer getColor(ItemStack stack, boolean canColor) {
		if (stack.isItemEqual(PAINT_CAN)) return ItemPaintCan.getColorFromStack(stack);
		else if (canColor && stack.isItemEqual(MILK_BUCKET)) return 0xFFFFFF;
		return null;
	}

	@Override
	public void onInventoryChanged(IInventory invent, int slotNumber) {
		if (!worldObj.isRemote) {

			boolean hasPaint = false;
			ItemStack can = inventory.getStackInSlot(Slots.paint);
			if (can != null) {
				Integer pickerColor = getColor(can, false);
				if (pickerColor != null && !logic.isWorking()) {
					color.set(pickerColor);
					// force GUI refresh
					color.markDirty();
				}

				Integer canColor = getColor(can, true);
				if (canColor != null) {
					this.canColor.set(canColor);
					hasPaint = true;
				}
			}
			flags.set(Flags.hasPaint, hasPaint);
			sync();

			markUpdated();
		}
	}

	private boolean hasStack(Slots slot, ItemStack stack) {
		ItemStack gotStack = inventory.getStackInSlot(slot);
		if (gotStack == null) { return false; }
		return gotStack.isItemEqual(stack);
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

	public IColorChanger createRpcProxy() {
		return createClientRpcProxy(IColorChanger.class);
	}

	@Override
	public ItemStack getPickBlock() {
		return getRawDrop();
	}

}
