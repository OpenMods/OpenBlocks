package openblocks.common.tileentity;

import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiAutoEnchantmentTable;
import openblocks.common.GenericInventory;
import openblocks.common.container.ContainerAutoEnchantmentTable;
import openblocks.utils.EnchantmentUtils;
import openblocks.utils.InventoryUtils;
import openblocks.utils.SlotSideHelper;
import openmods.common.api.IAwareTile;
import openmods.common.api.IHasGui;
import openmods.common.tileentity.SyncedTileEntity;
import openmods.network.sync.ISyncableObject;
import openmods.network.sync.SyncableFlags;
import openmods.network.sync.SyncableInt;
import openmods.network.sync.SyncableTank;

public class TileEntityAutoEnchantmentTable extends SyncedTileEntity
		implements IAwareTile, IFluidHandler, ISidedInventory, IHasGui {

	protected static final int TANK_CAPACITY = EnchantmentUtils.getLiquidForLevel(30);

	public static enum Slots {
		input,
		output
	}

	public static enum AutoSlots {
		input,
		output,
		xp
	}

	private SyncableTank tank;
	private SyncableFlags inputSides;
	private SyncableFlags outputSides;
	private SyncableFlags xpSides;
	private SyncableInt targetLevel;
	private SyncableFlags automaticSlots;

	private SlotSideHelper slotSides = new SlotSideHelper();

	/**
	 * grotesque book turning stuff taken from the main enchantment table
	 */
	public int tickCount;
	public float pageFlip;
	public float pageFlipPrev;
	public float field_70373_d;
	public float field_70374_e;
	public float bookSpread;
	public float bookSpreadPrev;
	public float bookRotation2;
	public float bookRotationPrev;
	public float bookRotation;
	private static Random rand = new Random();

	public TileEntityAutoEnchantmentTable() {
		setInventory(new GenericInventory("autoenchant", true, 2));
		slotSides.addMapping(Slots.input, inputSides);
		slotSides.addMapping(Slots.output, outputSides);
	}

	@Override
	protected void createSyncedFields() {
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.XP_FLUID);
		inputSides = new SyncableFlags();
		outputSides = new SyncableFlags();
		xpSides = new SyncableFlags();
		targetLevel = new SyncableInt(1);
		automaticSlots = new SyncableFlags();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		handleBookRotation();
		if (!worldObj.isRemote) {

			if (automaticSlots.get(AutoSlots.xp)) {
				tank.autoFillFromSides(80, this, xpSides);
			}

			if (shouldAutoOutput() && hasStack(Slots.output)) {
				InventoryUtils.moveItemsToOneOfSides(this, Slots.output, 1, outputSides);
			}

			// if we should auto input the tool and we don't currently have one
			if (shouldAutoInput() && !hasStack(Slots.input)) {
				InventoryUtils.moveItemsFromOneOfSides(this, null, 1, Slots.input, inputSides);
			}

			if (hasStack(Slots.input)
					&& isItemValidForSlot(Slots.input.ordinal(), getStack(Slots.input))
					&& !hasStack(Slots.output)) {
				int xpRequired = EnchantmentUtils.getLiquidForLevel(targetLevel.getValue());
				if (xpRequired > 0 && tank.getFluidAmount() >= xpRequired) {
					double power = EnchantmentUtils.getPower(worldObj, xCoord, yCoord, zCoord);
					int enchantability = EnchantmentUtils.calcEnchantability(getStack(Slots.input), (int)power, true);
					if (enchantability >= targetLevel.getValue()) {
						if (EnchantmentUtils.enchantItem(getStack(Slots.input), targetLevel.getValue(), worldObj.rand)) {
							tank.drain(xpRequired, true);
							ItemStack inputStack = getStack(Slots.input);
							setStack(Slots.input, null);
							setStack(Slots.output, inputStack.copy());
						}
					}
				}
			}
		}
	}

	private void handleBookRotation() {
		this.bookSpreadPrev = this.bookSpread;
		this.bookRotationPrev = this.bookRotation2;
		EntityPlayer entityplayer = this.worldObj.getClosestPlayer(this.xCoord + 0.5F, this.yCoord + 0.5F, this.zCoord + 0.5F, 3.0D);

		if (entityplayer != null) {
			double d0 = entityplayer.posX - (this.xCoord + 0.5F);
			double d1 = entityplayer.posZ - (this.zCoord + 0.5F);
			this.bookRotation = (float)Math.atan2(d1, d0);
			this.bookSpread += 0.1F;

			if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
				float f = this.field_70373_d;

				do {
					this.field_70373_d += rand.nextInt(4) - rand.nextInt(4);
				} while (f == this.field_70373_d);
			}
		} else {
			this.bookRotation += 0.02F;
			this.bookSpread -= 0.1F;
		}

		while (this.bookRotation2 >= (float)Math.PI) {
			this.bookRotation2 -= ((float)Math.PI * 2F);
		}

		while (this.bookRotation2 < -(float)Math.PI) {
			this.bookRotation2 += ((float)Math.PI * 2F);
		}

		while (this.bookRotation >= (float)Math.PI) {
			this.bookRotation -= ((float)Math.PI * 2F);
		}

		while (this.bookRotation < -(float)Math.PI) {
			this.bookRotation += ((float)Math.PI * 2F);
		}

		float f1 = this.bookRotation - this.bookRotation2;

		while (f1 >= (float)Math.PI)
			f1 -= ((float)Math.PI * 2F);

		while (f1 < -(float)Math.PI) {
			f1 += ((float)Math.PI * 2F);
		}

		this.bookRotation2 += f1 * 0.4F;

		if (this.bookSpread < 0.0F) {
			this.bookSpread = 0.0F;
		}

		if (this.bookSpread > 1.0F) {
			this.bookSpread = 1.0F;
		}

		++this.tickCount;
		this.pageFlipPrev = this.pageFlip;
		float f2 = (this.field_70373_d - this.pageFlip) * 0.4F;
		float f3 = 0.2F;

		if (f2 < -f3) {
			f2 = -f3;
		}

		if (f2 > f3) {
			f2 = f3;
		}

		this.field_70374_e += (f2 - this.field_70374_e) * 0.9F;
		this.pageFlip += this.field_70374_e;
	}

	private boolean shouldAutoInput() {
		return automaticSlots.get(AutoSlots.input);
	}

	private boolean shouldAutoOutput() {
		return automaticSlots.get(AutoSlots.output);
	}

	public boolean hasStack(Enum<?> slot) {
		return getStack(slot) != null;
	}

	public SyncableInt getTargetLevel() {
		return targetLevel;
	}

	public void setStack(Enum<?> slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot.ordinal(), stack);
	}

	public ItemStack getStack(Enum<?> slot) {
		return inventory.getStackInSlot(slot);
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
	public void onSynced(Set<ISyncableObject> changes) {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == Slots.input.ordinal()) { return !itemstack.isItemEnchanted(); }
		return i == Slots.input.ordinal();
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return slotSides.getSlotsForSide(side);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return slotSides.canInsertItem(i, j);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return slotSides.canExtractItem(i, j);
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

}
