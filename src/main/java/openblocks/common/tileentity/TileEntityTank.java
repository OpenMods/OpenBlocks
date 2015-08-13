package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.renderer.tileentity.tank.*;
import openblocks.common.LiquidXpUtils;
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

public class TileEntityTank extends SyncedTileEntity implements IActivateAwareTile, IPlacerAwareTile, INeighbourAwareTile, ICustomHarvestDrops {

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

				renderLogic.updateFluid(fluidStack);
			}
		}
	}

	private final TankRenderLogic renderLogic;

	@Override
	public void validate() {
		super.validate();
		if (worldObj.isRemote) renderLogic.initialize(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (worldObj.isRemote) renderLogic.clearConnections();
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

	public TileEntityTank() {
		syncMap.addSyncListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				ticksSinceLastSync = 0;
			}
		});

		syncMap.addUpdateListener(new RenderUpdateListeners());

		renderLogic = new TankRenderLogic(tank);
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

	public INeighbourMap getRenderNeigbourMap() {
		return new NeighbourMap(worldObj, xCoord, yCoord, zCoord, tank.getFluid());
	}

	public ITankRenderFluidData getRenderFluidData() {
		return renderLogic;
	}

	public ITankConnections getRenderConnectionsData() {
		return renderLogic;
	}

	public boolean accepts(FluidStack liquid) {
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
	public void onBlockPlacedBy(EntityLivingBase placer, ItemStack stack) {
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

	public TileEntityTank getTankInDirection(int dx, int dy, int dz) {
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

			int requiredXPJuice = LiquidXpUtils.xpToLiquidRatio(requiredXP);

			FluidStack drained = drain(direction, requiredXPJuice, false);
			if (drained != null) {
				int xp = LiquidXpUtils.liquidToXpRatio(drained.amount);
				if (xp > 0) {
					int actualDrain = LiquidXpUtils.xpToLiquidRatio(xp);
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

		if (worldObj.isRemote) renderLogic.validateConnections();
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
}
