package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiXPBottler;
import openblocks.common.container.ContainerXPBottler;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.liquids.SidedFluidHandler;
import openmods.sync.*;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.EnchantmentUtils;
import openmods.utils.InventoryUtils;
import openmods.utils.SidedInventoryAdapter;

public class TileEntityXPBottler extends SyncedTileEntity implements IInventoryProvider, IHasGui, IExtendable {

	protected static final int TANK_CAPACITY = EnchantmentUtils.XPToLiquidRatio(EnchantmentUtils.XP_PER_BOTTLE);
	protected static final ItemStack GLASS_BOTTLE = new ItemStack(Items.glass_bottle, 1);
	protected static final ItemStack XP_BOTTLE = new ItemStack(Items.experience_bottle, 1);
	public static final int PROGRESS_TICKS = 40;

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
			if (slot != 0) return false;
			return itemstack.getItem() == Items.glass_bottle;
		}
	};

	@IncludeInterface(ISidedInventory.class)
	private final SidedInventoryAdapter sided = new SidedInventoryAdapter(inventory);

	/** synced data objects **/
	private SyncableProgress progress;
	private SyncableFlags glassSides;
	private SyncableFlags xpBottleSides;
	private SyncableFlags xpSides;
	private SyncableFlags automaticSlots;
	private SyncableTank tank;

	@IncludeInterface
	private final IFluidHandler tankWrapper = new SidedFluidHandler.Drain(xpSides, tank);

	@Override
	protected void createSyncedFields() {
		progress = new SyncableProgress(PROGRESS_TICKS);
		glassSides = new SyncableFlags();
		xpBottleSides = new SyncableFlags();
		xpSides = new SyncableFlags();
		automaticSlots = new SyncableFlags();
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
				tank.fillFromSides(10, worldObj, getPosition(), xpSides);
			}

			// if they've ticked auto output, and we have something to output
			if (shouldAutoOutput() && hasOutputStack()) {
				InventoryUtils.moveItemsToOneOfSides(this, Slots.output, 1, xpBottleSides);
			}

			// if we should auto input and we don't have any glass in the slot
			if (shouldAutoInput() && !hasGlassInInput()) {
				InventoryUtils.moveItemsFromOneOfSides(this, GLASS_BOTTLE, 1, Slots.input, glassSides);
			}

			// if there's no space in the output, we've got no input bottles or
			// the tank isnt full, reset progress
			if (!hasSpaceInOutput() || !hasGlassInInput() || !isTankFull()) {
				progress.reset();
				return;
			}
			// while progress is moving, modify by 1
			if (!progress.isComplete()) {
				progress.increase();
			} else {
				// this happens when the progress has completed
				worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:fill", .5f, .8f);
				inventory.decrStackSize(Slots.input.ordinal(), 1);
				// drain the entire tank (it stores enough for 1 bottle)
				tank.drain(tank.getFluidAmount(), true);
				// increase the stacksize of the output slot
				if (inventory.getStackInSlot(Slots.output.ordinal()) == null) {
					inventory.setInventorySlotContents(Slots.output.ordinal(), XP_BOTTLE.copy());
				} else {
					ItemStack outputStack = inventory.getStackInSlot(Slots.output.ordinal()).copy();
					outputStack.stackSize++;
					inventory.setInventorySlotContents(Slots.output.ordinal(), outputStack);
				}
				// reset progress
				progress.reset();
			}
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

	public SyncableFlags getGlassSides() {
		return glassSides;
	}

	public SyncableFlags getXPBottleSides() {
		return xpBottleSides;
	}

	public SyncableFlags getXPSides() {
		return xpSides;
	}

	public SyncableFlags getAutomaticSlots() {
		return automaticSlots;
	}

	public SyncableProgress getProgress() {
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

	public IFluidTank getTank() {
		return tank;
	}

	@IncludeOverride
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

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
}
