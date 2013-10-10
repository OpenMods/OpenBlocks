package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.common.api.IAwareTile;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableInt;
import openblocks.utils.BlockUtils;
import openblocks.utils.EnchantmentUtils;

public class TileEntityXPBottler extends OpenTileEntity implements IAwareTile, ISidedInventory, IFluidHandler {

	private GenericInventory inventory = new GenericInventory("xpbottler", true, 2);

	private FluidTank tank = new FluidTank(EnchantmentUtils.XPToLiquidRatio(EnchantmentUtils.XP_PER_BOTTLE));

	private FluidStack xpFluid = new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1);

	private ItemStack glassBottle = new ItemStack(Item.glassBottle, 1);
	private ItemStack xpBottle = new ItemStack(Item.expBottle, 1);

	private SyncableInt progress = new SyncableInt();
	private SyncableFlags glassSides = new SyncableFlags();
	private SyncableFlags xpSides = new SyncableFlags();

	public static final int PROGRESS_TICKS = 40;

	public TileEntityXPBottler() {

	}

	public SyncableFlags getGlassSides() {
		return glassSides;
	}

	public SyncableFlags getXPSides() {
		return xpSides;
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

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (!hasSpaceInOutput() || !hasGlassInInput() || !isTankFull()) {
				progress.setValue(0);
				return;
			}
			if (progress.getValue() < PROGRESS_TICKS) {
				progress.modify(1);
			} else {
				inventory.decrStackSize(0, 1);
				tank.drain(tank.getFluidAmount(), true);
				if (inventory.getStackInSlot(1) == null) {
					inventory.setInventorySlotContents(1, xpBottle.copy());
				} else {
					ItemStack outputStack = inventory.getStackInSlot(1).copy();
					outputStack.stackSize++;
					inventory.setInventorySlotContents(1, outputStack);
				}
				progress.setValue(0);
			}
		}
	}

	public boolean hasGlassInInput() {
		ItemStack inputStack = inventory.getStackInSlot(0);
		return inputStack != null && inputStack.isItemEqual(glassBottle);
	}

	public boolean hasSpaceInOutput() {
		ItemStack outputStack = inventory.getStackInSlot(1);
		return outputStack == null || (outputStack.isItemEqual(xpBottle) && outputStack.stackSize < outputStack.getMaxStackSize());
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
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub

	}

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
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
		tank.readFromNBT(tag);
		progress.readFromNBT(tag, "progress");
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource != null && resource.isFluidEqual(xpFluid)) { return tank.fill(resource, doFill); }
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource == null) { return null; }
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

}
