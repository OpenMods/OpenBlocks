package openblocks.common.tileentity;

import java.util.EnumMap;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiPaintMixer;
import openblocks.common.container.ContainerPaintMixer;
import openblocks.common.item.ItemPaintCan;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.api.IInventoryCallback;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.sync.*;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.ColorUtils;

import com.google.common.collect.Maps;

public class TileEntityPaintMixer extends SyncedTileEntity implements IInventoryProvider, IHasGui, IInventoryCallback, IExtendable {

	private static final ItemStack PAINT_CAN = new ItemStack(OpenBlocks.Blocks.paintCan);
	private static final ItemStack MILK_BUCKET = new ItemStack(Items.milk_bucket);
	private static final int PROGRESS_TICKS = 300;

	public static enum Slots {
		paint,
		reserved, // old output slot, now merged with input
		dyeCyan,
		dyeMagenta,
		dyeYellow,
		dyeBlack
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
	private SyncableInt color;
	private boolean isWorking;
	private SyncableProgress progress;
	private SyncableFlags flags;
	private int chosenColor;
	// These could be optimized with a byte array later
	// Not important for release
	// Levels should be 0-2, so that if there is 0.3 left, 1 can be consumed and
	// not overflow ;)

	public SyncableFloat lvlCyan, lvlMagenta, lvlYellow, lvlBlack;

	private GenericInventory inventory = new GenericInventory("paintmixer", true, 6) {
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
	public void initialize() {
		if (!worldObj.isRemote) {
			sync();
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

			if (chosenColor != color.getValue()) {
				progress.reset();
				isWorking = false;
			}

			if (isWorking) {
				if (!hasValidInput() || !hasCMYK()) {
					progress.reset();
					isWorking = false;
					return;
				}
				if (!progress.isComplete()) {
					progress.increase();
				} else {
					consumeInk();
					ItemStack output = ItemPaintCan.createStack(color.getValue(), ItemPaintCan.FULL_CAN_SIZE);
					inventory.setInventorySlotContents(Slots.paint.ordinal(), output);
					canColor.setValue(color.getValue());
					progress.reset();
					isWorking = false;
				}
			}
			checkAutoConsumption();
			sync();
		}
	}

	private void checkAutoConsumption() {
		if (lvlCyan.getValue() <= 1f) { /* We can store 2.0, so <= */
			if (tryUseInk(Slots.dyeCyan, 1)) {
				lvlCyan.setValue(lvlCyan.getValue() + 1f);
			}
		}
		if (lvlMagenta.getValue() <= 1f) {
			if (tryUseInk(Slots.dyeMagenta, 1)) {
				lvlMagenta.setValue(lvlMagenta.getValue() + 1f);
			}
		}
		if (lvlYellow.getValue() <= 1f) {
			if (tryUseInk(Slots.dyeYellow, 1)) {
				lvlYellow.setValue(lvlYellow.getValue() + 1f);
			}
		}
		if (lvlBlack.getValue() <= 1f) {
			if (tryUseInk(Slots.dyeBlack, 1)) {
				lvlBlack.setValue(lvlBlack.getValue() + 1f);
			}
		}

	}

	public boolean hasCMYK() {
		return hasSufficientInk();
	}

	private void consumeInk() {
		ColorUtils.CYMK cymk = new ColorUtils.RGB(color.getValue()).toCYMK();
		lvlCyan.setValue(lvlCyan.getValue() - cymk.getCyan());
		lvlBlack.setValue(lvlBlack.getValue() - cymk.getKey());
		lvlYellow.setValue(lvlYellow.getValue() - cymk.getYellow());
		lvlMagenta.setValue(lvlMagenta.getValue() - cymk.getMagenta());
	}

	private boolean hasSufficientInk() {
		ColorUtils.CYMK cymk = new ColorUtils.RGB(color.getValue()).toCYMK();
		if (cymk.getCyan() > lvlCyan.getValue()) {
			if (tryUseInk(Slots.dyeCyan, 1)) {
				lvlCyan.setValue(lvlCyan.getValue() + 1f);
			} else {
				return false;
			}
		}
		if (cymk.getYellow() > lvlYellow.getValue()) {
			if (tryUseInk(Slots.dyeYellow, 1)) {
				lvlYellow.setValue(lvlYellow.getValue() + 1f);
			} else {
				return false;
			}
		}
		if (cymk.getMagenta() > lvlMagenta.getValue()) {
			if (tryUseInk(Slots.dyeMagenta, 1)) {
				lvlMagenta.setValue(lvlMagenta.getValue() + 1f);
			} else {
				return false;
			}
		}
		if (cymk.getKey() > lvlBlack.getValue()) {
			if (tryUseInk(Slots.dyeBlack, 1)) {
				lvlBlack.setValue(lvlBlack.getValue() + 1f);
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
		return allowedColor != null && stack != null && OreDictionary.getOreID(stack) == allowedColor;
	}

	@Override
	protected void createSyncedFields() {
		color = new SyncableInt(0xFF0000);
		flags = new SyncableFlags();
		progress = new SyncableProgress(PROGRESS_TICKS);
		lvlBlack = new SyncableFloat();
		lvlCyan = new SyncableFloat();
		lvlMagenta = new SyncableFloat();
		lvlYellow = new SyncableFloat();
		canColor = new SyncableInt(0xFFFFFF);
	}

	public SyncableInt getColor() {
		return color;
	}

	public boolean hasPaint() {
		return flags.get(Flags.hasPaint);
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

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

	public void tryStartMixer() {
		if (!worldObj.isRemote) {
			isWorking = true;
			chosenColor = color.getValue();
		}
	}

	public SyncableProgress getProgress() {
		return progress;
	}

	public int getCanColor() {
		return canColor.getValue();
	}

	public boolean isEnabled() {
		return progress.getValue() > 0;
	}

	public boolean hasValidInput() {
		return hasStack(Slots.paint, PAINT_CAN) || hasStack(Slots.paint, MILK_BUCKET);
	}

	private static Integer getColor(ItemStack stack) {
		if (stack.isItemEqual(PAINT_CAN)) return ItemPaintCan.getColorFromStack(stack);
		else if (stack.isItemEqual(MILK_BUCKET)) return 0xFFFFFF;
		return null;
	}

	@Override
	public void onInventoryChanged(IInventory invent, int slotNumber) {
		if (!worldObj.isRemote) {

			boolean hasPaint = false;
			ItemStack can = inventory.getStackInSlot(Slots.paint);
			if (can != null) {
				Integer c = getColor(can);
				if (c != null) {
					color.setValue(c);
					canColor.setValue(c);
					hasPaint = true;
				}
			}
			flags.set(Flags.hasPaint, hasPaint);
			sync();
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
}
