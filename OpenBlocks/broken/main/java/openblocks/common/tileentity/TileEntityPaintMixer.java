package openblocks.common.tileentity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiPaintMixer;
import openblocks.common.container.ContainerPaintMixer;
import openblocks.common.item.ItemPaintCan;
import openblocks.rpc.IColorChanger;
import openmods.OpenMods;
import openmods.api.IInventoryCallback;
import openmods.api.IValueProvider;
import openmods.colors.CYMK;
import openmods.colors.RGB;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.gamelogic.WorkerLogic;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryDelegate;
import openmods.inventory.TileEntityInventory;
import openmods.sync.SyncMap;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableFloat;
import openmods.sync.SyncableInt;
import openmods.sync.drops.DroppableTileEntity;
import openmods.sync.drops.StoreOnDrop;
import openmods.utils.MiscUtils;
import openmods.utils.OptionalInt;
import org.apache.commons.lang3.ArrayUtils;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityPaintMixer extends DroppableTileEntity implements IInventoryDelegate, IHasGui, IInventoryCallback, IColorChanger, ITickable {
	private static final ItemStack PAINT_CAN = new ItemStack(OpenBlocks.Blocks.paintCan);
	private static final ItemStack MILK_BUCKET = new ItemStack(Items.MILK_BUCKET);
	public static final int PROGRESS_TICKS = 300;

	private final IAnimationStateMachine asm;

	public enum Slots {
		paint,
		reserved, // old output slot, now merged with input
		dyeCyan,
		dyeMagenta,
		dyeYellow,
		dyeBlack
	}

	public enum DyeSlot {
		cyan,
		magenta,
		yellow,
		black
	}

	private static final EnumMap<Slots, Integer> ALLOWED_COLORS = Maps.newEnumMap(Slots.class);

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

	private final GenericInventory inventory = new TileEntityInventory(this, "paintmixer", true, 6) {
		@Override
		public boolean isItemValidForSlot(int slotId, @Nonnull ItemStack stack) {
			Slots[] values = Slots.values();
			if (stack.isEmpty() || slotId < 0 || slotId > values.length) return false;
			Slots slot = values[slotId];

			if (slot == Slots.paint) return PAINT_CAN.isItemEqual(stack) || MILK_BUCKET.isItemEqual(stack);
			return isValidForSlot(slot, stack);
		}
	};

	public TileEntityPaintMixer() {
		inventory.addCallback(this);
		this.asm = OpenMods.proxy.loadAsm(OpenBlocks.location("asms/block/paint_mixer.json"), ImmutableMap.of());
	}

	@Override
	public void update() {
		if (!world.isRemote) {

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
		CYMK cymk = new RGB(color.get()).toCYMK();
		lvlCyan.set(lvlCyan.get() - cymk.getCyan());
		lvlBlack.set(lvlBlack.get() - cymk.getKey());
		lvlYellow.set(lvlYellow.get() - cymk.getYellow());
		lvlMagenta.set(lvlMagenta.get() - cymk.getMagenta());
	}

	private boolean hasSufficientInk() {
		CYMK cymk = new RGB(color.get()).toCYMK();
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
		return isValidForSlot(slot, stack) && !inventory.decrStackSize(slot.ordinal(), consume).isEmpty();
	}

	private static boolean isValidForSlot(Slots slot, ItemStack stack) {
		Integer allowedColor = ALLOWED_COLORS.get(slot);
		if (allowedColor == null || stack.isEmpty()) return false;
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
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addUpdateListener(changes -> {
			if (asm != null && changes.contains(progress)) {
				final String expectedState = (progress.get() > 0)? "working" : "idle";
				if (!asm.currentState().equals(expectedState))
					asm.transition(expectedState);
			}
		});
	}

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerPaintMixer(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiPaintMixer(new ContainerPaintMixer(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return true;
	}

	@Override
	public void changeColor(int requestedColor) {
		if (!world.isRemote) {
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

	private static Integer getColor(@Nonnull ItemStack stack, boolean canColor) {
		if (stack.isItemEqual(PAINT_CAN)) return ItemPaintCan.getColorFromStack(stack);
		else if (canColor && stack.isItemEqual(MILK_BUCKET)) return 0xFFFFFF;
		return null;
	}

	@Override
	public void onInventoryChanged(IInventory invent, OptionalInt slotNumber) {
		if (!world.isRemote) {
			boolean hasPaint = false;
			ItemStack can = inventory.getStackInSlot(Slots.paint);
			if (!can.isEmpty()) {
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
		return !gotStack.isEmpty() && gotStack.isItemEqual(stack);
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		return tag;
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	public IColorChanger createRpcProxy() {
		return createClientRpcProxy(IColorChanger.class);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction side) {
		return capability == CapabilityAnimation.ANIMATION_CAPABILITY ||
				capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
				super.hasCapability(capability, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, Direction side) {
		if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
			return (T)asm;

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory.getHandler();

		return super.getCapability(capability, side);
	}

}
