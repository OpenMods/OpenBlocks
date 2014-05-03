package openblocks.common.tileentity;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.api.IActivateAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.liquids.GenericFluidHandler;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.Coord;
import openmods.utils.EnchantmentUtils;
import openmods.utils.ItemUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class TileEntityTank extends SyncedTileEntity implements IActivateAwareTile, IPlaceAwareTile, IExtendable {

	public class RenderContext {
		public EnumMap<ForgeDirection, TileEntityTank> neighbors = Maps.newEnumMap(ForgeDirection.class);

		public RenderContext() {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				TileEntityTank tank = getTankInDirection(dir);
				if (tank != null) neighbors.put(dir, tank);
			}
		}

		public double getLiquidHeightForSide(ForgeDirection... sides) {
			double renderHeight = getHeightForRender();
			if (renderHeight <= 0) return 0;
			if (renderHeight > 0.98) return 1.0;

			double fullness = renderHeight + getFlowOffset();
			int count = 1;
			final FluidStack fluid = tank.getFluid();
			for (ForgeDirection side : sides) {
				TileEntityTank sideTank = neighbors.get(side);
				if (sideTank != null && sideTank.canReceiveLiquid(fluid)) {
					fullness += sideTank.getHeightForRender() + sideTank.getFlowOffset();
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

	@IncludeInterface(IFluidHandler.class)
	private final GenericFluidHandler tankWrapper = new GenericFluidHandler(tank);

	@Override
	protected void createSyncedFields() {
		tank = new SyncableTank(getTankCapacity());
	}

	public static int getTankCapacity() {
		return FluidContainerRegistry.BUCKET_VOLUME * Config.bucketsPerTank;
	}

	public static final ForgeDirection[] horizontalDirections = new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };

	private static final Comparator<TileEntityTank> sortBySpace = new Comparator<TileEntityTank>() {
		@Override
		public int compare(TileEntityTank c1, TileEntityTank c2) {
			return c2.getSpace() - c1.getSpace();
		}
	};

	private static final Comparator<TileEntityTank> sortByAmount = new Comparator<TileEntityTank>() {
		@Override
		public int compare(TileEntityTank c1, TileEntityTank c2) {
			return c2.getAmount() - c1.getAmount();
		}
	};

	private TileEntityTank getTankInDirection(ForgeDirection direction) {
		TileEntity neighbor = getTileInDirection(direction);
		if (neighbor instanceof TileEntityTank && !neighbor.isInvalid()) return (TileEntityTank)neighbor;
		return null;
	}

	private List<TileEntityTank> getHorizontalTanks(Set<Coord> except) {
		List<TileEntityTank> horizontalTanks = Lists.newArrayList();
		Coord self = new Coord(xCoord, yCoord, zCoord);
		for (ForgeDirection direction : horizontalDirections) {
			Coord neigbor = self.getAdjacentCoord(direction);
			if (!except.contains(neigbor)) {
				TileEntity te = getTankInDirection(direction);
				if (te instanceof TileEntityTank) horizontalTanks.add((TileEntityTank)te);
			}
		}
		return horizontalTanks;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			Set<Coord> except = Sets.newHashSet(getPosition());
			if (tank.getFluidAmount() > 0) updateTankBelow(except);
			if (tank.getFluidAmount() > 0) updateHorizontalNeighbors(except);
			sync();
		} else {
			flowTimer += 0.1f;
		}
	}

	private void updateHorizontalNeighbors(Set<Coord> except) {
		List<TileEntityTank> horizontals = getHorizontalTanks(except);
		Collections.sort(horizontals, sortBySpace);
		for (TileEntityTank horizontal : horizontals) {
			FluidStack liquid = tank.getFluid();
			if (horizontal.canReceiveLiquid(liquid) && liquid != null) {
				int difference = tank.getFluidAmount() - horizontal.getTank().getFluidAmount();
				if (difference <= 1) continue;
				FluidStack liquidCopy = liquid.copy();
				liquidCopy.amount = Math.min(difference / 2, 500);
				int filled = horizontal.fill(liquidCopy, true, except);
				tank.drain(filled, true);
			}
		}
	}

	private void updateTankBelow(Set<Coord> except) {
		TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
		if (below != null && below.getSpace() > 0) {
			FluidStack myLiquid = tank.getFluid().copy();
			if (below.canReceiveLiquid(myLiquid)) {
				int toFill = Math.min(below.getSpace(), myLiquid.amount);
				myLiquid.amount = toFill;
				int filled = below.fill(myLiquid, true, except);
				tank.drain(filled, true);
			}
		}
	}

	private boolean canReceiveLiquid(FluidStack liquid) {
		if (liquid == null) return true;
		final FluidStack otherLiquid = tank.getFluid();
		return otherLiquid == null || otherLiquid.isFluidEqual(liquid);
	}

	public IFluidTank getTank() {
		return tank;
	}

	private int getSpace() {
		return tank.getSpace();
	}

	private int getAmount() {
		return tank.getFluidAmount();
	}

	private int fill(FluidStack resource, boolean doFill, Set<Coord> except) {
		if (resource == null) return 0;
		if (except == null) except = Sets.newHashSet();
		final Coord pos = getPosition();
		if (except.contains(pos)) return 0;
		except.add(pos);
		if (!canReceiveLiquid(resource)) return 0;

		resource = resource.copy();

		final int startAmount = resource.amount;
		tryFillTankBelow(resource, doFill, except);

		if (resource.amount <= 0) return startAmount;

		// fill myself up
		{
			int filled = tank.fill(resource, doFill);
			resource.amount -= filled;
		}

		if (resource.amount <= 0) return startAmount;
		tryFillHorizontals(resource, doFill, except);

		if (resource.amount <= 0) return startAmount;
		tryFillTankAbove(resource, doFill, except);

		return startAmount - resource.amount;
	}

	private void tryFillHorizontals(FluidStack resource, boolean doFill, Set<Coord> except) {
		List<TileEntityTank> horizontals = getHorizontalTanks(except);
		final int count = horizontals.size();
		if (count != 0) {
			final int amountPerSide = resource.amount / count;
			final int lastAmount = resource.amount - amountPerSide * (count - 1);
			// Last amount is either same or greater than normal, so we move
			// tank most likely to accept to end
			if (lastAmount != amountPerSide) Collections.sort(horizontals, sortByAmount);
			for (int i = 0; i < count; i++) {
				FluidStack copy = resource.copy();
				copy.amount = (i == count - 1)? lastAmount : amountPerSide;
				int filled = horizontals.get(i).fill(copy, doFill, except);
				resource.amount -= filled;
			}
		}
	}

	private void tryFillTankAbove(FluidStack resource, boolean doFill, Set<Coord> except) {
		TileEntityTank above = getTankInDirection(ForgeDirection.UP);
		if (above != null) {
			int filled = above.fill(resource, doFill, except);
			resource.amount -= filled;
		}
	}

	private void tryFillTankBelow(FluidStack resource, boolean doFill, Set<Coord> except) {
		TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
		if (below != null) {
			int filled = below.fill(resource, doFill, except);
			resource.amount -= filled;
		}
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		int newFluidId = tank.getFluid() == null? 0 : tank.getFluid().fluidID;
		if (newFluidId != previousFluidId) {
			worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		}
		previousFluidId = newFluidId;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("tank")) {
			NBTTagCompound tankTag = stack.getTagCompound().getCompoundTag("tank");
			this.tank.readFromNBT(tankTag);
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

		ForgeDirection direction = ForgeDirection.getOrientation(side);

		ItemStack current = player.inventory.getCurrentItem();
		if (current != null) {

			FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(current);

			// Handle filled containers
			if (liquid != null) {
				int qty = fill(direction, liquid, true);
				if (qty != 0 && !player.capabilities.isCreativeMode) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
				}
				return true;
			}
			FluidStack available = tank.getFluid();
			if (worldObj.isRemote && getTank().getFluidAmount() > 0) { return true; }
			if (available != null) {
				ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);
				liquid = FluidContainerRegistry.getFluidForFilledItem(filled);
				if (liquid != null) {
					if (!player.capabilities.isCreativeMode) {
						if (current.stackSize > 1) {
							if (!player.inventory.addItemStackToInventory(filled)) return false;
							player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
						} else {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
							player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
						}
					}
					tank.drain(liquid.amount, true);
					return true;
				}
			}
		} else {
			if (tank.getFluid() != null && tank.getFluid().isFluidEqual(OpenBlocks.XP_FLUID)) {
				int currentXP = EnchantmentUtils.getPlayerXP(player);
				int currentLevel = EnchantmentUtils.getLevelForExperience(currentXP);
				int nextXP = EnchantmentUtils.getExperienceForLevel(currentLevel + 1);
				int requiredXP = nextXP - currentXP;
				int requiredXPJuice = EnchantmentUtils.XPToLiquidRatio(requiredXP);
				FluidStack drained = tank.drain(requiredXPJuice, true);
				if (drained != null) {
					EnchantmentUtils.addPlayerXP(player, EnchantmentUtils.liquidToXPRatio(drained.amount));
				}
			}
		}
		return false;
	}

	public double getHeightForRender() {
		return (double)tank.getFluidAmount() / (double)tank.getCapacity();
	}

	public double getPercentFull() {
		return tank.getPercentFull();
	}

	public double getFlowOffset() {
		return Math.sin(flowTimer) / 35;
	}

	public NBTTagCompound getItemNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		tank.writeToNBT(nbt);
		return nbt;
	}

	@IncludeOverride
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.fill(resource, doFill, null);
	}

	public int getFluidLightLevel() {
		FluidStack fluid = tank.getFluid();
		if (fluid != null) {
			try {
				return fluid.getFluid().getLuminosity();
			} catch (Exception e) {}
		}
		return 0;
	}

	public RenderContext createRenderContext() {
		return new RenderContext();
	}
}
