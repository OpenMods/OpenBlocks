package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiAutoEnchantmentTable;
import openblocks.common.GenericInventory;
import openblocks.common.api.IAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.container.ContainerAutoEnchantmentTable;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableInt;
import openblocks.sync.SyncableTank;
import openblocks.utils.EnchantmentUtils;

public class TileEntityAutoEnchantmentTable extends NetworkedTileEntity
		implements IAwareTile, IFluidHandler, ISidedInventory, IHasGui {

	protected static final int TANK_CAPACITY = EnchantmentUtils.getLiquidForLevel(30);

	private GenericInventory inventory = new GenericInventory("autoenchant", true, 2);
	private SyncableTank tank;
	private SyncableFlags inputSides;
	private SyncableFlags outputSides;
	private SyncableFlags xpSides;
	private SyncableInt targetLevel;
	private SyncableFlags automaticSlots;

	public static enum Slots {
		input, output
	}

	public static enum AutoSlots {
		input, output, xp
	}

	public TileEntityAutoEnchantmentTable() {
		addSyncedObject(inputSides = new SyncableFlags());
		addSyncedObject(outputSides = new SyncableFlags());
		addSyncedObject(xpSides = new SyncableFlags());
		addSyncedObject(automaticSlots = new SyncableFlags());
		addSyncedObject(tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.XP_FLUID));
		addSyncedObject(targetLevel = new SyncableInt(1));
	}

	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

			if (automaticSlots.get(AutoSlots.xp)) {
				tank.autoFillFromSides(80, this, xpSides);
			}

			if (hasStack(Slots.input) && !hasStack(Slots.output)) {
				int xpRequired = EnchantmentUtils.getLiquidForLevel(targetLevel.getValue());
				if (xpRequired > 0 && tank.getFluidAmount() >= xpRequired) {
					double power = EnchantmentUtils.getPower(worldObj, xCoord, yCoord, zCoord);
					int enchantability = EnchantmentUtils.calcEnchantability(getStack(Slots.input), (int)power, true);
					if (enchantability >= targetLevel.getValue()) {
						if (EnchantmentUtils.enchantItem(getStack(Slots.input), targetLevel.getValue(), worldObj.rand)) {
							tank.drain(xpRequired, true);
							ItemStack inputStack = getStack(Slots.input);
							setStack(Slots.input, null);
							setStack(Slots.output, inputStack);
						}
					}
				}
			}
		}
	}

	public boolean hasStack(Enum<?> slot) {
		return getStack(slot) != null;
	}

	public void setStack(Enum<?> slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot.ordinal(), stack);
	}

	public ItemStack getStack(Enum<?> slot) {
		return inventory.getStackInSlot(slot.ordinal());
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerAutoEnchantmentTable(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiAutoEnchantmentTable(new ContainerAutoEnchantmentTable(player.inventory, this));
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {

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
		return inventory.getStackInSlotOnClosing(i);
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
		inventory.openChest();
	}

	@Override
	public void closeChest() {
		inventory.closeChest();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return inventory.getAccessibleSlotsFromSide(side);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return inventory.canInsertItem(i, itemstack, j);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return inventory.canExtractItem(i, itemstack, j);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource == null) { return null; }
		return drain(from, resource.amount, doDrain);
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
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public void onBlockBroken() {}

	@Override
	public void onBlockAdded() {}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		return false;
	}

	public IFluidTank getTank() {
		return tank;
	}

	public SyncableFlags getInputSides() {
		return inputSides;
	}

	public SyncableFlags getOutputSides() {
		return outputSides;
	}

	public SyncableFlags getXPSides() {
		return xpSides;
	}

	public SyncableFlags getAutomaticSlots() {
		return automaticSlots;
	}

}
