package openblocks.common.tileentity;

import static openblocks.common.item.ItemTankBlock.TANK_TAG;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.renderer.tileentity.tank.ITankConnections;
import openblocks.client.renderer.tileentity.tank.ITankRenderFluidData;
import openblocks.client.renderer.tileentity.tank.NeighbourMap;
import openblocks.client.renderer.tileentity.tank.TankRenderLogic;
import openblocks.common.FluidXpUtils;
import openblocks.common.FluidXpUtils.IFluidXpConverter;
import openmods.api.IActivateAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.liquids.ContainerBucketFillHandler;
import openmods.model.variant.VariantModelState;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncMap;
import openmods.sync.SyncableTank;
import openmods.sync.drops.DroppableTileEntity;
import openmods.sync.drops.StoreOnDrop;
import openmods.utils.EnchantmentUtils;

public class TileEntityTank extends DroppableTileEntity implements IActivateAwareTile, IPlaceAwareTile, INeighbourAwareTile, ITickableTileEntity {
	public static class BucketFillHandler extends ContainerBucketFillHandler {
		@Override
		protected boolean canFill(World world, BlockPos pos, TileEntity te) {
			return te instanceof TileEntityTank;
		}
	}

	private class RenderUpdateListeners implements ISyncListener {

		private FluidStack prevFluidStack = FluidStack.EMPTY;

		private int prevLuminosity;

		private boolean isSameFluid(FluidStack currentFluid) {
			return currentFluid.isFluidEqual(prevFluidStack);
		}

		@Override
		public void onSync(Set<ISyncableObject> changes) {
			if (changes.contains(tank)) {
				final FluidStack fluidStack = tank.getFluid();
				if (!isSameFluid(fluidStack)) {
					prevFluidStack = fluidStack;

					int luminosity = fluidStack.getFluid().getAttributes().getLuminosity(fluidStack);
					if (luminosity != prevLuminosity) {
						world.getChunkProvider().getLightManager().checkBlock(pos);
						prevLuminosity = luminosity;
					}
					needsNeighbourRecheck = true;
				}

				renderLogic.updateFluid(fluidStack);
			}
		}
	}

	private final TankRenderLogic renderLogic;
	private VariantModelState modelData = VariantModelState.EMPTY;
	private final IModelData modelDataWrapper = new ModelDataMap.Builder().withInitial(VariantModelState.PROPERTY, () -> modelData).build();

	@Override
	public void validate() {
		super.validate();

		needsNeighbourRecheck = true;
		if (world.isRemote) {
			renderLogic.initialize(world, pos);
		}
	}

	@Override
	public void remove() {
		super.remove();
		if (world.isRemote) {
			renderLogic.invalidateConnections();
		}
	}

	@Nullable
	protected TileEntityTank getNeighourTank(BlockPos pos) {
		if (!world.isBlockLoaded(pos)) {
			return null;
		}

		Chunk chunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
		TileEntity te = chunk.getTileEntity(pos, Chunk.CreateEntityType.CHECK);
		return (te instanceof TileEntityTank)? (TileEntityTank)te : null;
	}

	private static final int SYNC_THRESHOLD = 8;
	private static final int UPDATE_THRESHOLD = 20;

	@StoreOnDrop(name = TANK_TAG)
	private SyncableTank tank;

	private boolean hasPendingFluidTransfers = true;

	private int ticksSinceLastSync = hashCode() % SYNC_THRESHOLD;

	private boolean needsSync;

	private int ticksSinceLastNeighbourUpdate = hashCode() % UPDATE_THRESHOLD;

	private boolean hasPendingNeighbourUpdate;

	private boolean needsNeighbourRecheck;

	private final IFluidHandler tankCapabilityWrapper = new IFluidHandler() {

		@Override
		public int getTanks() {
			return 1;
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tankId) {
			return tank.getFluidInTank(tankId);
		}

		@Override
		public int getTankCapacity(int tankId) {
			return tank.getTankCapacity(tankId);
		}

		@Override
		public boolean isFluidValid(int tankId, @Nonnull FluidStack stack) {
			return tank.isFluidValid(tankId, stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (resource == null) {
				return 0;
			}
			FluidStack copy = resource.copy();
			fillColumn(copy, action);

			return resource.getAmount() - copy.getAmount();
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (maxDrain <= 0) {
				return FluidStack.EMPTY;
			}

			FluidStack contents = tank.getFluid();
			if (contents.isEmpty()) {
				return FluidStack.EMPTY;
			}

			FluidStack needed = contents.copy();
			needed.setAmount(maxDrain);

			drainFromColumn(needed, action);

			needed.setAmount(maxDrain - needed.getAmount());
			return needed;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (resource.isEmpty()) {
				return FluidStack.EMPTY;
			}

			FluidStack needed = resource.copy();
			drainFromColumn(needed, action);

			needed.setAmount(resource.getAmount() - needed.getAmount());
			return needed;
		}
	};

	public TileEntityTank() {
		super(OpenBlocks.TileEntities.tank);
		renderLogic = new TankRenderLogic(tank);
	}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addSyncListener(changes -> ticksSinceLastSync = 0);

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
		return FluidAttributes.BUCKET_VOLUME * Config.bucketsPerTank;
	}

	public int getFluidLightLevel() {
		FluidStack stack = tank.getFluid();
		Fluid fluid = stack.getFluid();
		return fluid.getAttributes().getLuminosity();
	}

	@Nullable
	public ITankRenderFluidData getRenderFluidData() {
		return renderLogic.getTankRenderData();
	}

	public ITankConnections getTankConnections() {
		return renderLogic.getTankConnections();
	}

	private void updateModelState() {
		modelData = new NeighbourMap(world, pos, tank.getFluid()).getState();
	}

	public boolean accepts(FluidStack liquid) {
		final FluidStack currentFluid = tank.getFluid();
		return currentFluid.isEmpty() || currentFluid.isFluidEqual(liquid);
	}

	private boolean containsFluid(FluidStack liquid) {
		return tank.getFluid().isFluidEqual(liquid);
	}

	public IFluidTank getTank() {
		return tank;
	}

	public CompoundNBT getItemNBT() {
		CompoundNBT nbt = new CompoundNBT();
		tank.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void onNeighbourChanged(BlockPos neighbourPos, Block neighbourBlock) {
		hasPendingFluidTransfers = true;
		needsNeighbourRecheck = true;
	}

	public void neighborChanged() {
		needsNeighbourRecheck = true;
	}

	@Override
	public void onBlockPlacedBy(BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
		CompoundNBT itemTag = stack.getTag();

		if (itemTag != null && itemTag.contains(TANK_TAG)) {
			tank.readFromNBT(itemTag.getCompound(TANK_TAG));
		}
	}

	@Nullable
	private static TileEntityTank getValidTank(final TileEntity neighbor) {
		return (neighbor instanceof TileEntityTank && !neighbor.isRemoved())? (TileEntityTank)neighbor : null;
	}

	@Nullable
	private TileEntityTank getTankInDirection(Direction direction) {
		final TileEntity neighbor = getTileInDirection(direction);
		return getValidTank(neighbor);
	}

	@Nullable
	public TileEntityTank getTankInDirection(int dx, int dy, int dz) {
		final TileEntity neighbor = getTileEntity(this.pos.add(dx, dy, dz));
		return getValidTank(neighbor);
	}

	@Override
	public ActionResultType onBlockActivated(PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (hand == Hand.MAIN_HAND) {
			final ItemStack heldItem = player.getHeldItemMainhand();
			if (!heldItem.isEmpty()) {
				final FluidActionResult result = tryEmptyItem(player, heldItem.copy());
				if (result.success) {
					if (!player.isCreative()) {
						player.setHeldItem(Hand.MAIN_HAND, result.result);
					}
					return ActionResultType.SUCCESS;
				}
			} else {
				return tryDrainXp(player)? ActionResultType.SUCCESS : ActionResultType.CONSUME;
			}
		}

		return ActionResultType.PASS;
	}

	protected boolean tryDrainXp(PlayerEntity player) {
		final FluidStack fluid = tank.getFluid();
		final Optional<IFluidXpConverter> maybeConverter = FluidXpUtils.getConverter(fluid);
		if (maybeConverter.isPresent()) {
			final IFluidXpConverter converter = maybeConverter.get();
			int requiredXp = MathHelper.ceil(player.xpBarCap() * (1 - player.experience));
			int requiredXpFluid = converter.xpToFluid(requiredXp);

			FluidStack drained = tankCapabilityWrapper.drain(requiredXpFluid, IFluidHandler.FluidAction.SIMULATE);
			if (!drained.isEmpty()) {
				int xp = converter.fluidToXp(drained.getAmount());
				if (xp > 0) {
					int actualDrain = converter.xpToFluid(xp);
					EnchantmentUtils.addPlayerXP(player, xp);
					tankCapabilityWrapper.drain(actualDrain, IFluidHandler.FluidAction.EXECUTE);
					return true;
				}
			}
		}

		return false;
	}

	protected FluidActionResult tryEmptyItem(PlayerEntity player, @Nonnull ItemStack container) {
		// not using FluidUtils.tryEmptyContainer, since it limits stack size to 1
		final LazyOptional<IFluidHandlerItem> containerFluidHandler = FluidUtil.getFluidHandler(container);
		return containerFluidHandler.map(itemContainer -> {
			FluidStack transfer = FluidUtil.tryFluidTransfer(tankCapabilityWrapper, itemContainer, FluidAttributes.BUCKET_VOLUME, true);
			if (!transfer.isEmpty()) {
				SoundEvent soundevent = transfer.getFluid().getAttributes().getEmptySound(transfer);
				player.playSound(soundevent, 1f, 1f);
				return new FluidActionResult(itemContainer.getContainer());
			}
			return FluidActionResult.FAILURE;
		}).orElse(FluidActionResult.FAILURE);
	}

	@Override
	public void tick() {
		ticksSinceLastSync++;
		ticksSinceLastNeighbourUpdate++;

		if (Config.shouldTanksUpdate && !world.isRemote && hasPendingFluidTransfers) {
			hasPendingFluidTransfers = false;

			FluidStack contents = tank.getFluid();
			if (!contents.isEmpty() && pos.getY() > 0) {
				tryFillBottomTank(contents);
				contents = tank.getFluid();
			}

			if (!contents.isEmpty()) {
				tryBalanceNeighbors(contents);
			}

			needsSync = true;
			markUpdated();
		}

		if (needsSync && !world.isRemote && ticksSinceLastSync > SYNC_THRESHOLD) {
			needsSync = false;
			sync();
		}

		if (hasPendingNeighbourUpdate && ticksSinceLastNeighbourUpdate > UPDATE_THRESHOLD) {
			hasPendingNeighbourUpdate = false;
			ticksSinceLastNeighbourUpdate = 0;
			world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
		}

		if (world.isRemote) {
			renderLogic.validateConnections(world, getPos());
		}

		if (needsNeighbourRecheck) {
			tank.updateNeighbours(world, pos);
			needsNeighbourRecheck = false;
			if (world.isRemote) {
				updateModelState();
				world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.RERENDER_MAIN_THREAD);
			}
		}
	}

	private void tryGetNeighbor(List<TileEntityTank> result, FluidStack fluid, Direction side) {
		TileEntityTank neighbor = getTankInDirection(side);
		if (neighbor != null && neighbor.accepts(fluid)) {
			result.add(neighbor);
		}
	}

	private void tryBalanceNeighbors(FluidStack contents) {
		List<TileEntityTank> neighbors = Lists.newArrayList();
		tryGetNeighbor(neighbors, contents, Direction.NORTH);
		tryGetNeighbor(neighbors, contents, Direction.SOUTH);
		tryGetNeighbor(neighbors, contents, Direction.EAST);
		tryGetNeighbor(neighbors, contents, Direction.WEST);

		final int count = neighbors.size();
		if (count == 0) {
			return;
		}

		int sum = contents.getAmount();
		for (TileEntityTank n : neighbors) {
			sum += n.tank.getFluidAmount();
		}

		final int suggestedAmount = sum / (count + 1);
		if (Math.abs(suggestedAmount - contents.getAmount()) < Config.tankFluidUpdateThreshold) {
			return; // Don't balance small amounts to reduce server load
		}

		FluidStack suggestedStack = contents.copy();
		suggestedStack.setAmount(suggestedAmount);

		for (TileEntityTank n : neighbors) {
			int amount = n.tank.getFluidAmount();
			int diff = amount - suggestedAmount;
			if (diff != 1 && diff != 0 && diff != -1) {
				n.tank.setFluid(suggestedStack.copy());
				n.tankChanged();
				sum -= suggestedAmount;
				n.hasPendingFluidTransfers = true;
			} else {
				sum -= amount;
			}
		}

		FluidStack s = tank.getFluid();
		if (sum != s.getAmount()) {
			s.setAmount(sum);
			tankChanged();
		}
	}

	private void notifyNeigbours() {
		hasPendingNeighbourUpdate = true;
	}

	private void tankChanged() {
		notifyNeigbours();
		tank.markDirty();
	}

	private void markContentsUpdated() {
		notifyNeigbours();
		hasPendingFluidTransfers = true;
	}

	private void tryFillBottomTank(FluidStack fluid) {
		TileEntity te = world.getTileEntity(pos.down());
		if (te instanceof TileEntityTank) {
			int amount = ((TileEntityTank)te).internalFill(fluid, IFluidHandler.FluidAction.EXECUTE);
			if (amount > 0) {
				internalDrain(amount, IFluidHandler.FluidAction.EXECUTE);
			}
		}
	}

	private FluidStack internalDrain(int amount, IFluidHandler.FluidAction action) {
		FluidStack drained = tank.drain(amount, action);
		if (!drained.isEmpty() && action.execute()) {
			markContentsUpdated();
		}
		return drained;
	}

	private void drainFromColumn(FluidStack needed, IFluidHandler.FluidAction doDrain) {
		if (!containsFluid(needed) || needed.isEmpty()) {
			return;
		}

		if (pos.getY() < 255) {
			TileEntity te = world.getTileEntity(pos.up());
			if (te instanceof TileEntityTank) {
				((TileEntityTank)te).drainFromColumn(needed, doDrain);
			}
		}

		if (needed.isEmpty()) {
			return;
		}

		FluidStack drained = internalDrain(needed.getAmount(), doDrain);
		if (drained.isEmpty()) {
			return;
		}

		needed.shrink(drained.getAmount());
	}

	private int internalFill(FluidStack resource, IFluidHandler.FluidAction action) {
		int amount = tank.fill(resource, action);
		if (amount > 0 && action.execute()) {
			markContentsUpdated();
		}
		return amount;
	}

	private void fillColumn(FluidStack resource, IFluidHandler.FluidAction action) {
		if (!accepts(resource) || resource.isEmpty()) {
			return;
		}

		int amount = internalFill(resource, action);

		resource.shrink(amount);

		if (!resource.isEmpty() && pos.getY() < 255) {
			TileEntity te = world.getTileEntity(pos.up());
			if (te instanceof TileEntityTank) {
				((TileEntityTank)te).fillColumn(resource, action);
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> tankCapabilityWrapper).cast();
		}

		return super.getCapability(capability, facing);
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		return modelDataWrapper;
	}

}
