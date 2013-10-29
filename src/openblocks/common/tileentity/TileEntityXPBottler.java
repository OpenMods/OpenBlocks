package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.common.GenericTank;
import openblocks.common.api.IAwareTile;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableInt;
import openblocks.utils.BlockUtils;
import openblocks.utils.EnchantmentUtils;
import openblocks.utils.InventoryUtils;

public class TileEntityXPBottler extends NetworkedTileEntity implements
		IAwareTile, ISidedInventory, IFluidHandler {

	protected static final int TANK_CAPACITY = EnchantmentUtils.XPToLiquidRatio(EnchantmentUtils.XP_PER_BOTTLE);
	protected static final ItemStack GLASS_BOTTLE = new ItemStack(Item.glassBottle, 1);
	protected static final ItemStack XP_BOTTLE = new ItemStack(Item.expBottle, 1);
	
	private GenericInventory inventory = new GenericInventory("xpbottler", true, 2);
	
	private GenericTank tank = new GenericTank(TANK_CAPACITY, OpenBlocks.XP_FLUID);

	public List<ForgeDirection> surroundingTanks = new ArrayList<ForgeDirection>();

	/** Ids of the data objects we'll sync **/
	public enum Keys {
		glassSides, xpBottleSides, xpSides, progress, tankLevel, autoFlags
	}

	public static enum AutoSides {
		input, output, xp
	}

	/** synced data objects **/
	private SyncableInt progress = new SyncableInt();
	private SyncableFlags glassSides = new SyncableFlags();
	private SyncableFlags xpBottleSides = new SyncableFlags();
	private SyncableFlags xpSides = new SyncableFlags();
	private SyncableInt tankLevel = new SyncableInt();
	private SyncableFlags autoFlags = new SyncableFlags();

	public static final int PROGRESS_TICKS = 40;

	public TileEntityXPBottler() {
		addSyncedObject(Keys.glassSides, glassSides);
		addSyncedObject(Keys.xpBottleSides, xpBottleSides);
		addSyncedObject(Keys.xpSides, xpSides);
		addSyncedObject(Keys.progress, progress);
		addSyncedObject(Keys.tankLevel, tankLevel);
		addSyncedObject(Keys.autoFlags, autoFlags);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

			// if we should, we'll autofill the tank
			if (autoFlags.get(AutoSides.xp)) {
				tank.autoFillFromSides(10, this, xpSides);
			}

			// if they've ticked auto output, and we have something to output
			if (shouldAutoOutput() && hasOutputStack()) {
				InventoryUtils.moveItemsToOneOfSides(this, 1, 1, xpBottleSides);
			}

			// if we should auto input and we don't have any glass in the slot
			if (shouldAutoInput() && !hasGlassInInput()) {
				InventoryUtils.moveItemsFromOneOfSides(this, GLASS_BOTTLE, 1, 0, glassSides);
			}

			// if there's no space in the output, we've got no input bottles or
			// the tank isnt full, reset progress
			if (!hasSpaceInOutput() || !hasGlassInInput() || !isTankFull()) {
				progress.setValue(0);
				return;
			}
			// while progress is moving, modify by 1
			if (progress.getValue() < PROGRESS_TICKS) {
				progress.modify(1);
			} else {
				// this happens when the progress has completed
				worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:fill", .5f, .8f);
				inventory.decrStackSize(0, 1);
				// drain the entire tank (it stores enough for 1 bottle)
				tank.drain(tank.getFluidAmount(), true);
				// increase the stacksize of the output slot
				if (inventory.getStackInSlot(1) == null) {
					inventory.setInventorySlotContents(1, XP_BOTTLE.copy());
				} else {
					ItemStack outputStack = inventory.getStackInSlot(1).copy();
					outputStack.stackSize++;
					inventory.setInventorySlotContents(1, outputStack);
				}
				// reset progress
				progress.setValue(0);
			}

		}
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

	public SyncableFlags getAutoFlags() {
		return autoFlags;
	}

	public SyncableInt getProgress() {
		return progress;
	}

	public double getProgressRatio() {
		int p = progress.getValue();
		if (p > 0) {
			p += progress.getTicksSinceChange();
		}
		p = Math.max(0, Math.min(p, PROGRESS_TICKS));
		return (double)p / (double)PROGRESS_TICKS;
	}

	public boolean hasOutputStack() {
		ItemStack outputStack = inventory.getStackInSlot(1);
		return outputStack != null && outputStack.stackSize > 0;
	}

	public boolean shouldAutoInput() {
		return autoFlags.get(AutoSides.input);
	}

	public boolean shouldAutoOutput() {
		return autoFlags.get(AutoSides.output);
	}

	public boolean hasGlassInInput() {
		ItemStack inputStack = inventory.getStackInSlot(0);
		return inputStack != null && inputStack.isItemEqual(GLASS_BOTTLE);
	}

	public boolean hasSpaceInOutput() {
		ItemStack outputStack = inventory.getStackInSlot(1);
		return outputStack == null
				|| (outputStack.isItemEqual(XP_BOTTLE) && outputStack.stackSize < outputStack.getMaxStackSize());
	}

	public boolean isTankFull() {
		return tank.getFluidAmount() == tank.getCapacity();
	}

	public FluidTank getTank() {
		return tank;
	}

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player, OpenBlocks.Gui.XPBottler);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		setRotation(BlockUtils.get2dOrientation(player));
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
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
	public ItemStack decrStackSize(int stackIndex, int byAmount) {
		return inventory.decrStackSize(stackIndex, byAmount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
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
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		tank.writeToNBT(tag);
		progress.writeToNBT(tag, "progress");
		glassSides.writeToNBT(tag, "glasssides");
		xpBottleSides.writeToNBT(tag, "xpbottlesides");
		xpSides.writeToNBT(tag, "xpsides");
		autoFlags.writeToNBT(tag, "autoflags");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
		tank.readFromNBT(tag);
		progress.readFromNBT(tag, "progress");
		glassSides.readFromNBT(tag, "glasssides");
		xpBottleSides.readFromNBT(tag, "xpbottlesides");
		xpSides.readFromNBT(tag, "xpsides");
		autoFlags.readFromNBT(tag, "autoflags");
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
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
	public void onSynced(List<ISyncableObject> changes) {
		// TODO Auto-generated method stub

	}

	public double getXPBufferRatio() {
		return Math.max(0, Math.min(1, (double)tankLevel.getValue()
				/ (double)tank.getCapacity()));
	}

	public void updateGuiValues() {
		tankLevel.setValue(tank.getFluidAmount());
	}

}
