package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiXPBottler;
import openblocks.common.GenericInventory;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.container.ContainerXPBottler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableProgress;
import openblocks.sync.SyncableTank;
import openblocks.utils.EnchantmentUtils;
import openblocks.utils.InventoryUtils;

public class TileEntityXPBottler extends SyncedTileEntity implements IActivateAwareTile, ISidedInventory, IFluidHandler, IHasGui {

	protected static final int TANK_CAPACITY = EnchantmentUtils.XPToLiquidRatio(EnchantmentUtils.XP_PER_BOTTLE);
	protected static final ItemStack GLASS_BOTTLE = new ItemStack(Item.glassBottle, 1);
	protected static final ItemStack XP_BOTTLE = new ItemStack(Item.expBottle, 1);
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

	/** synced data objects **/
	private SyncableProgress progress;
	private SyncableFlags glassSides;
	private SyncableFlags xpBottleSides;
	private SyncableFlags xpSides;
	private SyncableFlags automaticSlots;
	private SyncableTank tank;

	public TileEntityXPBottler() {
		setInventory(new GenericInventory("xpbottler", true, 2));
	}
	
	@Override
	protected void createSyncedFields() {
		progress = new SyncableProgress(PROGRESS_TICKS);
		glassSides = new SyncableFlags();
		xpBottleSides = new SyncableFlags();
		xpSides = new SyncableFlags();
		automaticSlots = new SyncableFlags();
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.XP_FLUID);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

			// if we should, we'll autofill the tank
			if (automaticSlots.get(AutoSlots.xp)) {
				tank.autoFillFromSides(10, this, xpSides);
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

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player);
		}
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		boolean sideAllowsGlass = false;
		boolean sideAllowsXP = false;
		if (glassSides.getActiveSlots().contains(side)) {
			sideAllowsGlass = true;
		}
		if (xpBottleSides.getActiveSlots().contains(side)) {
			sideAllowsXP = true;
		}
		if (sideAllowsXP && sideAllowsGlass) { return new int[] { 0, 1 }; }
		if (sideAllowsXP) { return new int[] { 1 }; }
		if (sideAllowsGlass) { return new int[] { 0 }; }

		return new int[0];
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		Item item = itemstack.getItem();
		if (item == null) { return false; }
		// slot 0, glass bottle, side accessible
		return i == 0 && item == Item.glassBottle && glassSides.get(j);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		Item item = itemstack.getItem();
		if (item == null) { return false; }
		if (item == Item.glassBottle) { return i == 0 && glassSides.get(j); }
		if (item == Item.expBottle) { return i == 1 && xpBottleSides.get(j); }
		return false;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {}

}
