package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiXPBottler;
import openblocks.common.container.ContainerXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler.AutoSlots;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.api.IValueProvider;
import openmods.api.IValueReceiver;
import openmods.gamelogic.WorkerLogic;
import openmods.gui.misc.IConfigurableGuiSlots;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.liquids.SidedFluidHandler;
import openmods.sync.*;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.*;
import openmods.utils.bitmap.*;

public class TileEntityXPBottler extends SyncedTileEntity implements IInventoryProvider, IHasGui, IExtendable, IConfigurableGuiSlots<AutoSlots> {

	public static final int TANK_CAPACITY = EnchantmentUtils.XPToLiquidRatio(EnchantmentUtils.XP_PER_BOTTLE);
	public static final int PROGRESS_TICKS = 40;

	protected static final ItemStack GLASS_BOTTLE = new ItemStack(Items.glass_bottle, 1);
	protected static final ItemStack XP_BOTTLE = new ItemStack(Items.experience_bottle, 1);

	public List<ForgeDirection> surroundingTanks = new ArrayList<ForgeDirection>();

	public static enum Slots {
		input,
		output
	}

	public static enum AutoSlots {
		input,
		output,
		xp
	}

	private final GenericInventory inventory = new GenericInventory("xpbottler", true, 2) {
		@Override
		public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
			if (slot != Slots.input.ordinal()) return false;
			return itemstack.getItem() == Items.glass_bottle;
		}
	};

	@IncludeInterface(ISidedInventory.class)
	private final SidedInventoryAdapter sided = new SidedInventoryAdapter(inventory);

	/** synced data objects **/
	private SyncableInt progress;
	private SyncableDirs glassSides;
	private SyncableDirs xpBottleSides;
	private SyncableDirs xpSides;
	private SyncableFlags automaticSlots;
	private SyncableTank tank;

	private final WorkerLogic logic = new WorkerLogic(progress, PROGRESS_TICKS);

	@IncludeInterface
	private final IFluidHandler tankWrapper = new SidedFluidHandler.Drain(xpSides, tank);

	@Override
	protected void createSyncedFields() {
		progress = new SyncableInt();
		glassSides = new SyncableDirs();
		xpBottleSides = new SyncableDirs();
		xpSides = new SyncableDirs();
		automaticSlots = SyncableFlags.create(AutoSlots.values().length);
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.XP_FLUID);
	}

	public TileEntityXPBottler() {
		sided.registerSlot(Slots.input, glassSides, true, false);
		sided.registerSlot(Slots.output, xpBottleSides, false, true);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

			// if we should, we'll autofill the tank
			if (automaticSlots.get(AutoSlots.xp)) {
				tank.fillFromSides(10, worldObj, getPosition(), xpSides.getValue());
			}

			// if they've ticked auto output, and we have something to output
			if (shouldAutoOutput() && hasOutputStack()) {
				InventoryUtils.moveItemsToOneOfSides(this, inventory, Slots.output.ordinal(), 1, xpBottleSides.getValue());
			}

			// if we should auto input and we don't have any glass in the slot
			if (shouldAutoInput() && !hasGlassInInput()) {
				InventoryUtils.moveItemsFromOneOfSides(this, inventory, GLASS_BOTTLE, 1, Slots.input.ordinal(), glassSides.getValue());
			}

			logic.checkWorkCondition(hasSpaceInOutput() && hasGlassInInput() && isTankFull());

			if (logic.update()) {
				// this happens when the progress has completed
				worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:bottler.signal", .5f, .8f);
				inventory.decrStackSize(Slots.input.ordinal(), 1);
				tank.setFluid(null);

				ItemStack outputStack = inventory.getStackInSlot(Slots.output.ordinal());

				if (outputStack == null) {
					inventory.setInventorySlotContents(Slots.output.ordinal(), XP_BOTTLE.copy());
				} else {
					outputStack.stackSize++;
				}

				inventory.onInventoryChanged(Slots.output.ordinal());
			}

			sync();
		}
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerXPBottler(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiXPBottler(new ContainerXPBottler(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	public IValueProvider<Integer> getProgress() {
		return progress;
	}

	public boolean hasOutputStack() {
		ItemStack outputStack = inventory.getStackInSlot(1);
		return outputStack != null && outputStack.stackSize > 0;
	}

	public boolean shouldAutoInput() {
		return automaticSlots.get(AutoSlots.input);
	}

	public boolean shouldAutoOutput() {
		return automaticSlots.get(AutoSlots.output);
	}

	public boolean hasGlassInInput() {
		ItemStack inputStack = inventory.getStackInSlot(Slots.input.ordinal());
		return inputStack != null && inputStack.isItemEqual(GLASS_BOTTLE);
	}

	public boolean hasSpaceInOutput() {
		ItemStack outputStack = inventory.getStackInSlot(Slots.output.ordinal());
		return outputStack == null
				|| (outputStack.isItemEqual(XP_BOTTLE) && outputStack.stackSize < outputStack.getMaxStackSize());
	}

	public boolean isTankFull() {
		return tank.getFluidAmount() == tank.getCapacity();
	}

	public IValueProvider<FluidStack> getFluidProvider() {
		return tank;
	}

	@IncludeOverride
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
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

	private SyncableDirs selectSlotMap(AutoSlots slot) {
		switch (slot) {
			case input:
				return glassSides;
			case output:
				return xpBottleSides;
			case xp:
				return xpSides;
			default:
				throw MiscUtils.unhandledEnum(slot);
		}
	}

	@Override
	public IValueProvider<Set<ForgeDirection>> createAllowedDirectionsProvider(AutoSlots slot) {
		return selectSlotMap(slot);
	}

	@Override
	public IWriteableBitMap<ForgeDirection> createAllowedDirectionsReceiver(AutoSlots slot) {
		SyncableDirs dirs = selectSlotMap(slot);
		return BitMapUtils.createRpcAdapter(createRpcProxy(dirs, IRpcDirectionBitMap.class));
	}

	@Override
	public IValueProvider<Boolean> createAutoFlagProvider(AutoSlots slot) {
		return BitMapUtils.singleBitProvider(automaticSlots, slot.ordinal());
	}

	@Override
	public IValueReceiver<Boolean> createAutoSlotReceiver(AutoSlots slot) {
		IRpcIntBitMap bits = createRpcProxy(automaticSlots, IRpcIntBitMap.class);
		return BitMapUtils.singleBitReceiver(bits, slot.ordinal());
	}
}
