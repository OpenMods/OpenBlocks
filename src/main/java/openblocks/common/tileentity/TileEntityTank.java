package openblocks.common.tileentity;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTankBlock;
import openmods.api.IActivateAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.liquids.GenericFluidHandler;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.EnchantmentUtils;
import openmods.utils.ItemUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TileEntityTank extends SyncedTileEntity implements IActivateAwareTile, IPlaceAwareTile, INeighbourAwareTile, IExtendable {

	public class RenderContext {
		public EnumMap<ForgeDirection, TileEntityTank> neighbors = Maps.newEnumMap(ForgeDirection.class);

		public RenderContext() {
			FluidStack ownFluid = tank.getFluid();

			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				TileEntityTank tank = getTankInDirection(dir);
				if (tank != null && tank.accepts(ownFluid)) neighbors.put(dir, tank);
			}
		}

		public double getLiquidHeightForSide(ForgeDirection... sides) {
			double renderHeight = getFluidRatio();
			if (renderHeight <= 0.02) return 0.02;
			if (renderHeight > 0.98) return 1.0;

			double fullness = renderHeight + getFlowOffset();
			int count = 1;
			final FluidStack fluid = tank.getFluid();
			for (ForgeDirection side : sides) {
				TileEntityTank sideTank = neighbors.get(side);
				if (sideTank != null && sideTank.accepts(fluid)) {
					fullness += sideTank.getFluidRatio() + sideTank.getFlowOffset();
					count++;
				}
			}
			return Math.max(0, Math.min(1, fullness / count));
		}

		public boolean hasNeighbor(ForgeDirection side) {
			return neighbors.containsKey(side);
		}
	}

	private SyncableTank tank;

	private double flowTimer = Math.random() * 100;

	private int previousFluidId = 0;

	private boolean forceUpdate = true;

	@IncludeInterface(IFluidHandler.class)
	private final GenericFluidHandler tankWrapper = new GenericFluidHandler(tank);

	@Override
	protected void createSyncedFields() {
		tank = new SyncableTank(getTankCapacity());
	}

	public double getFluidRatio() {
		return (double)tank.getFluidAmount() / (double)tank.getCapacity();
	}

	private double getFlowOffset() {
		return Math.sin(flowTimer) / 35;
	}

	public static int getTankCapacity() {
		return FluidContainerRegistry.BUCKET_VOLUME * Config.bucketsPerTank;
	}

	public int getFluidLightLevel() {
		FluidStack stack = tank.getFluid();
		if (stack != null) {
			Fluid fluid = stack.getFluid();
			if (fluid != null) return fluid.getLuminosity();
		}

		return 0;
	}

	public RenderContext createRenderContext() {
		return new RenderContext();
	}

	private boolean accepts(FluidStack liquid) {
		if (liquid == null) return true;
		final FluidStack ownFluid = tank.getFluid();
		return ownFluid == null || ownFluid.isFluidEqual(liquid);
	}

	private boolean containsFluid(FluidStack liquid) {
		if (liquid == null) return false;
		final FluidStack ownFluid = tank.getFluid();
		return ownFluid != null && ownFluid.isFluidEqual(liquid);
	}

	public IFluidTank getTank() {
		return tank;
	}

	public NBTTagCompound getItemNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		tank.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		int newFluidId = tank.getFluid() == null? -1 : tank.getFluid().fluidID;
		if (newFluidId != previousFluidId) worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		previousFluidId = newFluidId;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		forceUpdate = true;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		NBTTagCompound itemTag = stack.getTagCompound();

		if (itemTag != null && itemTag.hasKey(ItemTankBlock.TANK_TAG)) {
			tank.readFromNBT(itemTag.getCompoundTag(ItemTankBlock.TANK_TAG));
		}
	}

	private TileEntityTank getTankInDirection(ForgeDirection direction) {
		TileEntity neighbor = getTileInDirection(direction);
		if (neighbor instanceof TileEntityTank && !neighbor.isInvalid()) return (TileEntityTank)neighbor;
		return null;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		ForgeDirection direction = ForgeDirection.getOrientation(side);

		ItemStack usedItem = player.inventory.getCurrentItem();
		if (usedItem != null) return tryUseFluidContainer(player, direction, usedItem);

		return tryDrainXp(player, direction);
	}

	protected boolean tryDrainXp(EntityPlayer player, ForgeDirection direction) {
		if (tank.getFluid() != null && tank.getFluid().isFluidEqual(OpenBlocks.XP_FLUID)) {
			int currentXP = EnchantmentUtils.getPlayerXP(player);
			int nextLevelXP = EnchantmentUtils.getExperienceForLevel(player.experienceLevel + 1);
			int requiredXP = nextLevelXP - currentXP;

			int requiredXPJuice = EnchantmentUtils.XPToLiquidRatio(requiredXP);

			FluidStack drained = drain(direction, requiredXPJuice, false);
			if (drained != null) {
				int xp = EnchantmentUtils.liquidToXPRatio(drained.amount);
				if (xp > 0) {
					int actualDrain = EnchantmentUtils.XPToLiquidRatio(xp);
					EnchantmentUtils.addPlayerXP(player, xp);
					drain(direction, actualDrain, true);
					return true;
				}
			}
		}

		return false;
	}

	protected boolean tryUseFluidContainer(EntityPlayer player, ForgeDirection direction, ItemStack current) {
		return tryEmptyItem(player, direction, current) || tryFillItem(player, direction, current);
	}

	protected boolean tryFillItem(EntityPlayer player, ForgeDirection direction, ItemStack current) {
		FluidStack available = tank.getFluid();
		if (available == null || available.amount <= 0) return false;
		if (worldObj.isRemote) return true;

		ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);
		FluidStack containedFluid = FluidContainerRegistry.getFluidForFilledItem(filled);
		if (containedFluid != null) {
			if (!player.capabilities.isCreativeMode) {
				if (current.stackSize > 1) {
					if (!player.inventory.addItemStackToInventory(filled)) return false;
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
				} else {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
				}
			}
			drain(direction, containedFluid.amount, true);
			return true;
		}

		return false;
	}

	protected boolean tryEmptyItem(EntityPlayer player, ForgeDirection direction, ItemStack current) {
		FluidStack containedFluid = FluidContainerRegistry.getFluidForFilledItem(current);

		if (containedFluid != null) {
			int qty = fill(direction, containedFluid, true);
			if (qty != 0 && !player.capabilities.isCreativeMode) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
			}
			return true;
		}

		return false;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (Config.shouldTanksUpdate && !worldObj.isRemote && forceUpdate) {
			forceUpdate = false;

			FluidStack contents = tank.getFluid();
			if (contents != null && contents.amount > 0 && yCoord > 0) {
				tryFillBottomTank(contents);
				contents = tank.getFluid();
			}

			if (contents != null && contents.amount > 0) {
				tryBalanceNeighbors(contents);
			}

			sync();
		} else {
			flowTimer += 0.1f;
		}
	}

	private void tryGetNeighbor(List<TileEntityTank> result, FluidStack fluid, ForgeDirection side) {
		TileEntityTank neighbor = getTankInDirection(side);
		if (neighbor != null && neighbor.accepts(fluid)) result.add(neighbor);
	}

	private void tryBalanceNeighbors(FluidStack contents) {
		List<TileEntityTank> neighbors = Lists.newArrayList();
		tryGetNeighbor(neighbors, contents, ForgeDirection.NORTH);
		tryGetNeighbor(neighbors, contents, ForgeDirection.SOUTH);
		tryGetNeighbor(neighbors, contents, ForgeDirection.EAST);
		tryGetNeighbor(neighbors, contents, ForgeDirection.WEST);

		final int count = neighbors.size();
		if (count == 0) return;

		int sum = contents.amount;
		for (TileEntityTank n : neighbors)
			sum += n.tank.getFluidAmount();

		final int suggestedAmount = sum / (count + 1);
		FluidStack suggestedStack = contents.copy();
		suggestedStack.amount = suggestedAmount;

		for (TileEntityTank n : neighbors) {
			int amount = n.tank.getFluidAmount();
			int diff = amount - suggestedAmount;
			if (diff != 1 && diff != 0 && diff != -1) {
				n.tank.setFluid(suggestedStack.copy());
				n.tankChanged();
				sum -= suggestedAmount;
				n.forceUpdate = true;
			} else {
				sum -= amount;
			}
		}

		FluidStack s = tank.getFluid();
		if (sum != s.amount) {
			s.amount = sum;
			tankChanged();
		}
	}

	private void tankChanged() {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType().blockID);
		tank.markDirty();
	}

	private void markUpdated() {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType().blockID);
		forceUpdate = true;
	}

	private void tryFillBottomTank(FluidStack fluid) {
		TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
		if (te instanceof TileEntityTank) {
			int amount = ((TileEntityTank)te).internalFill(fluid, true);
			if (amount > 0) internalDrain(amount, true);
		}
	}

	private FluidStack internalDrain(int amount, boolean doDrain) {
		FluidStack drained = tank.drain(amount, doDrain);
		if (drained != null && doDrain) markUpdated();
		return drained;
	}

	private void drainFromColumn(FluidStack needed, boolean doDrain) {
		if (!containsFluid(needed) || needed.amount <= 0) return;

		if (yCoord < 255) {
			TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
			if (te instanceof TileEntityTank) ((TileEntityTank)te).drainFromColumn(needed, doDrain);
		}

		if (needed.amount <= 0) return;

		FluidStack drained = internalDrain(needed.amount, doDrain);
		if (drained == null) return;

		needed.amount -= drained.amount;
	}

	private int internalFill(FluidStack resource, boolean doFill) {
		int amount = tank.fill(resource, doFill);
		if (amount > 0 && doFill) markUpdated();
		return amount;
	}

	private void fillColumn(FluidStack resource, boolean doFill) {
		if (!accepts(resource) || resource.amount <= 0) return;

		int amount = internalFill(resource, doFill);

		resource.amount -= amount;

		if (resource.amount > 0 && yCoord < 255) {
			TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
			if (te instanceof TileEntityTank) ((TileEntityTank)te).fillColumn(resource, doFill);
		}
	}

	@IncludeOverride
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource == null) return null;

		FluidStack needed = resource.copy();
		drainFromColumn(needed, doDrain);

		needed.amount = resource.amount - needed.amount;
		return needed;
	}

	@IncludeOverride
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (maxDrain <= 0) return null;

		FluidStack contents = tank.getFluid();
		if (contents == null || contents.amount <= 0) return null;

		FluidStack needed = contents.copy();
		needed.amount = maxDrain;

		drainFromColumn(needed, doDrain);

		needed.amount = maxDrain - needed.amount;
		return needed;
	}

	@IncludeOverride
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource == null) return 0;
		FluidStack copy = resource.copy();
		fillColumn(copy, doFill);

		return resource.amount - copy.amount;
	}
}
