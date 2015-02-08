package openblocks.common.tileentity;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;
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
import openmods.utils.Diagonal;
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

	private static final float PHASE_PER_DISTANCE = 0.5f;

	private static final float WAVE_AMPLITUDE = 0.01f;

	private static final float WAVE_FREQUENCY = 0.1f;

	public interface ITankRenderData {
		public FluidStack getFluid();

		public boolean hasFluid();

		public boolean shouldRenderFluidWall(ForgeDirection side);

		public float getCornerFluidLevel(Diagonal diagonal, float time);

		public float getCenterFluidLevel(float time);
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

				for (Map.Entry<Diagonal, DiagonalConnection> e : diagonalConnections.entrySet())
					e.getValue().updateFluid(e.getKey().getOpposite(), fluidStack);

				for (Map.Entry<ForgeDirection, HorizontalConnection> e : horizontalConnections.entrySet())
					e.getValue().updateFluid(e.getKey().getOpposite(), fluidStack);

				topConnection.updateBottomFluid(fluidStack, tank.getSpace() == 0);
				bottomConnection.updateTopFluid(fluidStack);
			}
		}
	}

	private static class DiagonalConnection {

		private static class Group {
			private final FluidStack fluid;
			private final Set<Diagonal> diagonals = EnumSet.noneOf(Diagonal.class);

			private float sum;

			public Group(FluidStack fluid) {
				this.fluid = fluid;
			}

			public boolean match(FluidStack stack) {
				return fluid.isFluidEqual(stack);
			}

			public void addDiagonal(Diagonal diagonal, FluidStack stack) {
				diagonals.add(diagonal);
				sum += stack.amount;
			}

			public void update(float[] height) {
				final float average = clampLevel((sum / diagonals.size()) / getTankCapacity());

				for (Diagonal d : diagonals)
					height[d.ordinal()] = average;
			}
		}

		private final float phase;

		private final Map<Diagonal, FluidStack> fluids = Maps.newEnumMap(Diagonal.class);

		private final float[] height = new float[4];

		public DiagonalConnection(float phase) {
			this.phase = phase;
		}

		public float getRenderHeight(Diagonal corner, float time) {
			float h = height[corner.ordinal()];
			if (h <= 0) return 0;

			return clampLevel(calculateWaveAmplitude(time, phase) + h);
		}

		public void updateFluid(Diagonal corner, FluidStack stack) {
			fluids.put(corner, safeCopy(stack));
			recalculate();
		}

		public void clearFluid(Diagonal corner) {
			fluids.remove(corner);
			recalculate();
		}

		private static Group findGroup(List<Group> entries, FluidStack stack) {
			for (Group group : entries)
				if (group.match(stack)) return group;

			Group newGroup = new Group(stack);
			entries.add(newGroup);
			return newGroup;
		}

		private void recalculate() {
			forceZero();

			List<Group> groups = Lists.newArrayList();
			for (Diagonal diagonal : Diagonal.VALUES) {
				if (!fluids.containsKey(diagonal)) continue;

				FluidStack stack = fluids.get(diagonal);

				if (stack == null || stack.amount <= 0) return;

				Group e = findGroup(groups, stack);
				e.addDiagonal(diagonal, stack);
			}

			for (Group group : groups)
				group.update(height);
		}

		private void forceZero() {
			height[0] = height[1] = height[2] = height[3] = 0;
		}
	}

	private interface Connection {
		public boolean isConnected();
	}

	private static class VerticalConnection implements Connection {

		private FluidStack fluidTop;

		private FluidStack fluidBottom;

		private boolean bottomIsFull;

		private boolean isConnected;

		@Override
		public boolean isConnected() {
			return isConnected;
		}

		public void updateTopFluid(FluidStack stack) {
			this.fluidTop = safeCopy(stack);
			updateConnection();
		}

		public void clearTopFluid() {
			this.fluidTop = null;
			this.isConnected = false;
		}

		public void updateBottomFluid(FluidStack stack, boolean isFull) {
			this.fluidBottom = safeCopy(stack);
			this.bottomIsFull = isFull;
			updateConnection();
		}

		public void clearBottomFluid() {
			this.fluidBottom = null;
			this.bottomIsFull = false;
			this.isConnected = false;
		}

		private void updateConnection() {
			boolean sameLiquid = fluidTop != null && fluidBottom != null && fluidTop.isFluidEqual(fluidBottom);
			this.isConnected = sameLiquid && bottomIsFull;
		}
	}

	private static class HorizontalConnection implements Connection {

		private FluidStack fluidA;

		private FluidStack fluidB;

		private boolean isConnected;

		@Override
		public boolean isConnected() {
			return isConnected;
		}

		public void updateFluid(ForgeDirection direction, FluidStack stack) {
			if (direction == ForgeDirection.NORTH || direction == ForgeDirection.WEST) this.fluidA = safeCopy(stack);
			else this.fluidB = safeCopy(stack);

			this.isConnected = fluidA != null && fluidB != null && fluidA.isFluidEqual(fluidB);
		}

		public void clearFluid(ForgeDirection direction) {
			if (direction == ForgeDirection.NORTH || direction == ForgeDirection.WEST) this.fluidA = null;
			else this.fluidB = null;

			this.isConnected = false;
		}
	}

	private Map<Diagonal, DiagonalConnection> diagonalConnections = Maps.newEnumMap(Diagonal.class);

	private Map<ForgeDirection, HorizontalConnection> horizontalConnections = Maps.newEnumMap(ForgeDirection.class);

	private VerticalConnection topConnection;

	private VerticalConnection bottomConnection;

	private float phase;

	private static FluidStack safeCopy(FluidStack stack) {
		return stack != null? stack : null;
	}

	private static float clampLevel(float level) {
		if (level <= 0.1f) return 0.1f;
		if (level >= 0.9f) return 1.0f;
		return level;
	}

	private boolean shouldRenderFluidWall(ForgeDirection side) {
		switch (side) {
			case DOWN:
				return !bottomConnection.isConnected();
			case UP:
				return !topConnection.isConnected();
			case EAST:
			case WEST:
			case NORTH:
			case SOUTH: {
				HorizontalConnection connection = horizontalConnections.get(side);
				return !connection.isConnected();
			}
			default:
				return true;
		}
	}

	private float getCornerFluidLevel(Diagonal corner, float time) {
		DiagonalConnection diagonal = diagonalConnections.get(corner);
		return diagonal.getRenderHeight(corner.getOpposite(), time);
	}

	private float calculatePhase(Diagonal diagonal) {
		float posX = xCoord + diagonal.offsetX / 2.0f;
		float posY = yCoord + diagonal.offsetY / 2.0f;
		float posZ = zCoord + diagonal.offsetZ / 2.0f;
		return (posX + posY + posZ) * PHASE_PER_DISTANCE;
	}

	private static float calculateWaveAmplitude(float time, float phase) {
		return MathHelper.sin(time * WAVE_FREQUENCY + phase) * WAVE_AMPLITUDE;
	}

	@Override
	public void validate() {
		super.validate();

		if (worldObj.isRemote) {
			final TileEntityTank tankN = getNeighbourTank(ForgeDirection.NORTH);
			final TileEntityTank tankS = getNeighbourTank(ForgeDirection.SOUTH);
			final TileEntityTank tankW = getNeighbourTank(ForgeDirection.WEST);
			final TileEntityTank tankE = getNeighbourTank(ForgeDirection.EAST);

			final TileEntityTank tankNE = getNeighbourTank(Diagonal.NE);
			final TileEntityTank tankNW = getNeighbourTank(Diagonal.NW);
			final TileEntityTank tankSE = getNeighbourTank(Diagonal.SE);
			final TileEntityTank tankSW = getNeighbourTank(Diagonal.SW);

			tryHorizontalConnection(tankN, ForgeDirection.NORTH);
			tryHorizontalConnection(tankS, ForgeDirection.SOUTH);
			tryHorizontalConnection(tankW, ForgeDirection.WEST);
			tryHorizontalConnection(tankE, ForgeDirection.EAST);

			tryCornerConnection(tankN, tankNW, tankW, Diagonal.NW);
			tryCornerConnection(tankW, tankSW, tankS, Diagonal.SW);
			tryCornerConnection(tankE, tankNE, tankN, Diagonal.NE);
			tryCornerConnection(tankS, tankSE, tankE, Diagonal.SE);

			final TileEntityTank tankT = getNeighbourTank(ForgeDirection.UP);
			tryTopConnection(tankT);

			final TileEntityTank tankB = getNeighbourTank(ForgeDirection.DOWN);
			tryBottomConnection(tankB);

			phase = (xCoord + yCoord + zCoord) * PHASE_PER_DISTANCE;
		}
	}

	private void tryCornerConnection(TileEntityTank tankCW, TileEntityTank tankD, TileEntityTank tankCCW, Diagonal dir) {
		DiagonalConnection connection = findConnection(tankCW, tankD, tankCCW, dir);
		diagonalConnections.put(dir, connection);

	}

	private DiagonalConnection findConnection(TileEntityTank tankCW, TileEntityTank tankD, TileEntityTank tankCCW, Diagonal dir) {
		Diagonal start = dir;

		dir = dir.rotateCW();
		if (tankCW != null) return tankCW.diagonalConnections.get(dir);

		dir = dir.rotateCW();
		if (tankD != null) return tankD.diagonalConnections.get(dir);

		dir = dir.rotateCW();
		if (tankCCW != null) return tankCCW.diagonalConnections.get(dir);

		return new DiagonalConnection(calculatePhase(start));
	}

	protected void tryHorizontalConnection(TileEntityTank neighbour, ForgeDirection dir) {
		HorizontalConnection connection = (neighbour != null)? neighbour.horizontalConnections.get(dir.getOpposite()) : new HorizontalConnection();
		horizontalConnections.put(dir, connection);
	}

	protected void tryBottomConnection(TileEntityTank neighbour) {
		bottomConnection = neighbour != null? neighbour.topConnection : new VerticalConnection();
	}

	protected void tryTopConnection(TileEntityTank neighbour) {
		topConnection = neighbour != null? neighbour.bottomConnection : new VerticalConnection();
	}

	private TileEntityTank getNeighbourTank(ForgeDirection dir) {
		return getNeighourTank(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
	}

	private TileEntityTank getNeighbourTank(Diagonal dir) {
		return getNeighourTank(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
	}

	@Override
	public void invalidate() {
		if (worldObj.isRemote) {
			for (Map.Entry<Diagonal, DiagonalConnection> e : diagonalConnections.entrySet())
				e.getValue().clearFluid(e.getKey().getOpposite());
			diagonalConnections.clear();

			for (Map.Entry<ForgeDirection, HorizontalConnection> e : horizontalConnections.entrySet())
				e.getValue().clearFluid(e.getKey().getOpposite());
			horizontalConnections.clear();

			topConnection.clearBottomFluid();
			bottomConnection.clearTopFluid();
		}
	}

	protected TileEntityTank getNeighourTank(final int x, final int y, final int z) {
		if (!worldObj.blockExists(x, y, z)) return null;

		Chunk chunk = worldObj.getChunkFromBlockCoords(x, z);
		TileEntity te = chunk.getTileEntityUnsafe(x & 0xF, y, z & 0xF);
		return (te instanceof TileEntityTank)? (TileEntityTank)te : null;
	}

	private static final int SYNC_THRESHOLD = 8;
	private static final int UPDATE_THRESHOLD = 20;

	private SyncableTank tank;

	private boolean forceUpdate = true;

	private int ticksSinceLastSync = hashCode() % SYNC_THRESHOLD;

	private boolean needsSync;

	private int ticksSinceLastUpdate = hashCode() % UPDATE_THRESHOLD;

	private boolean needsUpdate;

	@IncludeInterface(IFluidHandler.class)
	private final GenericFluidHandler tankWrapper = new GenericFluidHandler(tank);

	private final ITankRenderData renderData = new ITankRenderData() {
		@Override
		public boolean hasFluid() {
			return tank.getFluidAmount() > 0;
		}

		@Override
		public FluidStack getFluid() {
			return tank.getFluid();
		}

		@Override
		public boolean shouldRenderFluidWall(ForgeDirection side) {
			return TileEntityTank.this.shouldRenderFluidWall(side);
		}

		@Override
		public float getCornerFluidLevel(Diagonal diagonal, float time) {
			return TileEntityTank.this.getCornerFluidLevel(diagonal, time);
		}

		@Override
		public float getCenterFluidLevel(float time) {
			final float raw = (float)tank.getFluidAmount() / tank.getCapacity();
			final float waving = calculateWaveAmplitude(time, phase) + raw;
			return clampLevel(waving);
		}
	};

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
		return pass == 0;
	}

	public ITankRenderData getRenderData() {
		return renderData;
	}
}
