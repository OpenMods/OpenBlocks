package openblocks.common.tileentity;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTankBlock;
import openmods.api.*;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.liquids.GenericFluidHandler;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.EnchantmentUtils;
import openmods.utils.ItemUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TileEntityTank extends SyncedTileEntity implements IActivateAwareTile, IPlaceAwareTile, INeighbourAwareTile, ICustomHarvestDrops {

	public static final int DIR_NORTH = 1;
	public static final int DIR_SOUTH = 2;

	public static final int DIR_WEST = 4;
	public static final int DIR_EAST = 8;

	public static final int DIR_UP = 16;
	public static final int DIR_DOWN = 32;

	public interface IFluidHeightCalculator {
		public double calculateHeight(ForgeDirection sideA, ForgeDirection sideB);

		public boolean shouldRenderFluidWall(ForgeDirection side);
	}

	private abstract class HeightCalculatorBase implements IFluidHeightCalculator {
		protected final Set<ForgeDirection> renderedWalls = EnumSet.noneOf(ForgeDirection.class);

		protected boolean hasCommonTopWall(FluidStack fluid) {
			if (isFull()) {
				TileEntityTank topNeighbour = getTankInDirection(ForgeDirection.UP);
				if (topNeighbour != null) {
					FluidStack otherFluid = topNeighbour.tank.getFluid();
					return fluid.isFluidEqual(otherFluid);
				}
			}

			return false;
		}

		protected boolean hasCommonBottomWall(FluidStack fluid) {
			TileEntityTank bottomNeighbour = getTankInDirection(ForgeDirection.DOWN);
			if (bottomNeighbour != null && bottomNeighbour.isFull()) {
				FluidStack otherFluid = bottomNeighbour.tank.getFluid();
				return fluid.isFluidEqual(otherFluid);
			}

			return false;
		}

		protected void updateTopBottomNeighbours(FluidStack fluid) {
			if (fluid != null) {
				if (hasCommonTopWall(fluid)) renderedWalls.add(ForgeDirection.UP);
				if (hasCommonBottomWall(fluid)) renderedWalls.add(ForgeDirection.DOWN);
			}
		}

		@Override
		public boolean shouldRenderFluidWall(ForgeDirection side) {
			return renderedWalls.contains(side);
		}
	}

	private class ConstantHeightCalculator extends HeightCalculatorBase {
		private final double height;

		public ConstantHeightCalculator(FluidStack fluid, double height) {
			this.height = height;

			updateValue(fluid, ForgeDirection.EAST);
			updateValue(fluid, ForgeDirection.WEST);
			updateValue(fluid, ForgeDirection.NORTH);
			updateValue(fluid, ForgeDirection.SOUTH);

			updateTopBottomNeighbours(fluid);
		}

		private void updateValue(FluidStack fluid, ForgeDirection dir) {
			TileEntityTank tank = getTankInDirection(dir);
			if (tank != null && tank.accepts(fluid)) renderedWalls.add(dir);
		}

		@Override
		public double calculateHeight(ForgeDirection sideA, ForgeDirection sideB) {
			return height;
		}
	}

	private class AveragingHeightCalculator extends HeightCalculatorBase {
		private final double ownAmount;
		private final Map<ForgeDirection, Double> neighbours = Maps.newEnumMap(ForgeDirection.class);

		public AveragingHeightCalculator(FluidStack ownFluid, double ownHeight) {
			ownAmount = ownHeight + getFlowOffset();

			updateValue(ownFluid, ForgeDirection.EAST);
			updateValue(ownFluid, ForgeDirection.WEST);
			updateValue(ownFluid, ForgeDirection.NORTH);
			updateValue(ownFluid, ForgeDirection.SOUTH);

			updateTopBottomNeighbours(ownFluid);
		}

		private void updateValue(FluidStack fluid, ForgeDirection dir) {
			TileEntityTank tank = getTankInDirection(dir);
			if (tank != null && tank.accepts(fluid)) {
				double amount = tank.getFluidRatio() + tank.getFlowOffset();
				neighbours.put(dir, amount);
				renderedWalls.add(dir);
			}
		}

		@Override
		public double calculateHeight(ForgeDirection sideA, ForgeDirection sideB) {
			double sum = ownAmount;

			int count = 1;

			Double heightA = neighbours.get(sideA);
			if (heightA != null) {
				count++;
				sum += heightA;
			}

			Double heightB = neighbours.get(sideB);
			if (heightB != null) {
				count++;
				sum += heightB;
			}

			return Math.max(MIN_FLUID_HEIGHT, Math.min(1, sum / count));
		}
	}

	public interface IRenderNeighbours {
		public boolean hasDirectNeighbour(int direction);

		public boolean hasDiagonalNeighbour(int direction1, int direction2);
	}

	public static final IRenderNeighbours NO_NEIGHBOURS = new IRenderNeighbours() {
		@Override
		public boolean hasDirectNeighbour(int dir) {
			return false;
		}

		@Override
		public boolean hasDiagonalNeighbour(int direction1, int direction2) {
			return false;
		}
	};

	private class NeighbourProvider implements IRenderNeighbours {
		public boolean[] neighbors = new boolean[64];

		private void testNeighbour(FluidStack ownFluid, int dx, int dy, int dz, int flag) {
			TileEntityTank tank = getTankInDirection(dx, dy, dz);
			if (tank != null && tank.accepts(ownFluid)) neighbors[flag] = true;
		}

		public NeighbourProvider() {
			final FluidStack fluid = tank.getFluid();

			testNeighbour(fluid, 0, 1, 0, DIR_UP);
			testNeighbour(fluid, 0, -1, 0, DIR_DOWN);
			testNeighbour(fluid, +1, 0, 0, DIR_EAST);
			testNeighbour(fluid, -1, 0, 0, DIR_WEST);
			testNeighbour(fluid, 0, 0, +1, DIR_SOUTH);
			testNeighbour(fluid, 0, 0, -1, DIR_NORTH);

			testNeighbour(fluid, +1, 1, 0, DIR_UP | DIR_EAST);
			testNeighbour(fluid, -1, 1, 0, DIR_UP | DIR_WEST);
			testNeighbour(fluid, 0, 1, +1, DIR_UP | DIR_SOUTH);
			testNeighbour(fluid, 0, 1, -1, DIR_UP | DIR_NORTH);

			testNeighbour(fluid, +1, -1, 0, DIR_DOWN | DIR_EAST);
			testNeighbour(fluid, -1, -1, 0, DIR_DOWN | DIR_WEST);
			testNeighbour(fluid, 0, -1, +1, DIR_DOWN | DIR_SOUTH);
			testNeighbour(fluid, 0, -1, -1, DIR_DOWN | DIR_NORTH);

			testNeighbour(fluid, -1, 0, -1, DIR_WEST | DIR_NORTH);
			testNeighbour(fluid, -1, 0, +1, DIR_WEST | DIR_SOUTH);
			testNeighbour(fluid, +1, 0, +1, DIR_EAST | DIR_SOUTH);
			testNeighbour(fluid, +1, 0, -1, DIR_EAST | DIR_NORTH);
		}

		@Override
		public boolean hasDirectNeighbour(int direction) {
			return neighbors[direction];
		}

		@Override
		public boolean hasDiagonalNeighbour(int direction1, int direction2) {
			return neighbors[direction1 | direction2];
		}
	}

	private class RenderUpdateListeners implements ISyncListener {

		private FluidStack prevFluidStack;

		private int prevLuminosity;

		private boolean isSameFluid(FluidStack currentFluid) {
			if (currentFluid == null) return prevFluidStack == null;
			return currentFluid.isFluidEqual(prevFluidStack);
		}

		@Override
		public void onSync(Set<ISyncableObject> changes) {
			if (changes.contains(tank)) {
				final FluidStack fluidStack = tank.getFluid();
				if (!isSameFluid(fluidStack)) {
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					prevFluidStack = fluidStack;

					int luminosity = fluidStack != null? fluidStack.getFluid().getLuminosity(fluidStack) : 0;
					if (luminosity != prevLuminosity) {
						worldObj.func_147451_t(xCoord, yCoord, zCoord);
						prevLuminosity = luminosity;
					}
				}
			}
		}
	}

	private static final double MIN_FLUID_HEIGHT = 0.02;
	private static final int SYNC_THRESHOLD = 8;
	private static final int UPDATE_THRESHOLD = 20;

	private SyncableTank tank;

	private double flowTimer = Math.random() * 100;

	private boolean forceUpdate = true;

	private int ticksSinceLastSync = hashCode() % SYNC_THRESHOLD;

	private boolean needsSync;

	private int ticksSinceLastUpdate = hashCode() % UPDATE_THRESHOLD;

	private boolean needsUpdate;

	@IncludeInterface(IFluidHandler.class)
	private final GenericFluidHandler tankWrapper = new GenericFluidHandler(tank);

	public TileEntityTank() {
		syncMap.addSyncListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				ticksSinceLastSync = 0;
			}
		});

		syncMap.addUpdateListener(new RenderUpdateListeners());
	}

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

	public IFluidHeightCalculator getRenderFluidHeights() {
		FluidStack fluid = tank.getFluid();
		if (fluid == null || fluid.amount <= 0) return new ConstantHeightCalculator(fluid, 0);

		final double renderHeight = getFluidRatio();
		if (renderHeight <= MIN_FLUID_HEIGHT) return new ConstantHeightCalculator(fluid, MIN_FLUID_HEIGHT);
		if (renderHeight > 0.98) return new ConstantHeightCalculator(fluid, 1.0);
		return new AveragingHeightCalculator(fluid, renderHeight);
	}

	public IRenderNeighbours getRenderConnections() {
		return new NeighbourProvider();
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

	private boolean isFull() {
		return tank.getSpace() == 0;
	}

	public NBTTagCompound getItemNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		tank.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void onNeighbourChanged(Block block) {
		forceUpdate = true;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		NBTTagCompound itemTag = stack.getTagCompound();

		if (itemTag != null && itemTag.hasKey(ItemTankBlock.TANK_TAG)) {
			tank.readFromNBT(itemTag.getCompoundTag(ItemTankBlock.TANK_TAG));
		}
	}

	private static TileEntityTank getValidTank(final TileEntity neighbor) {
		return (neighbor instanceof TileEntityTank && !neighbor.isInvalid())? (TileEntityTank)neighbor : null;
	}

	private TileEntityTank getTankInDirection(ForgeDirection direction) {
		final TileEntity neighbor = getTileInDirection(direction);
		return getValidTank(neighbor);
	}

	private TileEntityTank getTankInDirection(int dx, int dy, int dz) {
		final TileEntity neighbor = getNeighbour(dx, dy, dz);
		return getValidTank(neighbor);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		ForgeDirection direction = ForgeDirection.getOrientation(side);
		ItemStack usedItem = player.inventory.getCurrentItem();
		if (usedItem != null) return tryEmptyItem(player, direction, usedItem);
		if (worldObj.isRemote) return false;

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
		return tryEmptyItem(player, direction, current);
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

		ticksSinceLastSync++;
		ticksSinceLastUpdate++;

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

			needsSync = true;
			markUpdated();
		} else {
			flowTimer += 0.1f;
		}

		if (needsSync && !worldObj.isRemote && ticksSinceLastSync > SYNC_THRESHOLD) {
			needsSync = false;
			sync();
		}

		if (needsUpdate && ticksSinceLastUpdate > UPDATE_THRESHOLD) {
			needsUpdate = false;
			ticksSinceLastUpdate = 0;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
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

	private void notifyNeigbours() {
		needsUpdate = true;
	}

	private void tankChanged() {
		notifyNeigbours();
		tank.markDirty();
	}

	private void markContentsUpdated() {
		notifyNeigbours();
		forceUpdate = true;
	}

	private void tryFillBottomTank(FluidStack fluid) {
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
		if (te instanceof TileEntityTank) {
			int amount = ((TileEntityTank)te).internalFill(fluid, true);
			if (amount > 0) internalDrain(amount, true);
		}
	}

	private FluidStack internalDrain(int amount, boolean doDrain) {
		FluidStack drained = tank.drain(amount, doDrain);
		if (drained != null && doDrain) markContentsUpdated();
		return drained;
	}

	private void drainFromColumn(FluidStack needed, boolean doDrain) {
		if (!containsFluid(needed) || needed.amount <= 0) return;

		if (yCoord < 255) {
			TileEntity te = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
			if (te instanceof TileEntityTank) ((TileEntityTank)te).drainFromColumn(needed, doDrain);
		}

		if (needed.amount <= 0) return;

		FluidStack drained = internalDrain(needed.amount, doDrain);
		if (drained == null) return;

		needed.amount -= drained.amount;
	}

	private int internalFill(FluidStack resource, boolean doFill) {
		int amount = tank.fill(resource, doFill);
		if (amount > 0 && doFill) markContentsUpdated();
		return amount;
	}

	private void fillColumn(FluidStack resource, boolean doFill) {
		if (!accepts(resource) || resource.amount <= 0) return;

		int amount = internalFill(resource, doFill);

		resource.amount -= amount;

		if (resource.amount > 0 && yCoord < 255) {
			TileEntity te = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
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

	@Override
	public boolean suppressNormalHarvestDrops() {
		return true;
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops) {
		ItemStack stack = new ItemStack(OpenBlocks.Blocks.tank);

		if (tank.getFluidAmount() > 0) {
			NBTTagCompound tankTag = getItemNBT();
			NBTTagCompound itemTag = ItemUtils.getItemTag(stack);
			itemTag.setTag("tank", tankTag);
		}

		drops.add(stack);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
}
