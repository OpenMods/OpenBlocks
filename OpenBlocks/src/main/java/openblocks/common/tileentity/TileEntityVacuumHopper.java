package openblocks.common.tileentity;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import openblocks.OpenBlocks;
import openblocks.common.FluidXpUtils;
import openblocks.common.container.ContainerVacuumHopper;
import openmods.api.IActivateAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.api.IValueProvider;
import openmods.inventory.GenericInventory;
import openmods.inventory.ISidedInventoryDelegate;
import openmods.inventory.ItemMover;
import openmods.inventory.TileEntityInventory;
import openmods.liquids.SidedFluidCapabilityWrapper;
import openmods.model.variant.VariantModelState;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncMap;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableSides;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.EnchantmentUtils;
import openmods.utils.InventoryUtils;
import openmods.utils.ItemUtils;
import openmods.utils.SidedInventoryAdapter;
import openmods.utils.SidedItemHandlerAdapter;
import openmods.utils.bitmap.BitMapUtils;
import openmods.utils.bitmap.IReadableBitMap;
import openmods.utils.bitmap.IRpcDirectionBitMap;
import openmods.utils.bitmap.IWriteableBitMap;

public class TileEntityVacuumHopper extends SyncedTileEntity implements ISidedInventoryDelegate, IActivateAwareTile, INeighbourAwareTile, ITickableTileEntity, INamedContainerProvider {
	private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("openblocks.gui.vacuumhopper");

	public static final int TANK_CAPACITY = FluidXpUtils.xpJuiceConverter.xpToFluid(EnchantmentUtils.getExperienceForLevel(5));

	public static final String OUTPUT_ITEMS = "items";
	public static final String OUTPUT_FLUIDS = "fluids";
	public static final String OUTPUT_BOTH = "both";

	private SyncableTank tank;
	public SyncableSides xpOutputs;
	public SyncableSides itemOutputs;
	public SyncableBoolean vacuumDisabled;

	private boolean needsTankUpdate;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, 10));

	private final SidedInventoryAdapter sided = new SidedInventoryAdapter(inventory);

	private final SidedItemHandlerAdapter itemHandlerCapability = new SidedItemHandlerAdapter(inventory.getHandler());

	private final SidedFluidCapabilityWrapper tankCapability = SidedFluidCapabilityWrapper.wrap(tank, xpOutputs, true, false);

	private VariantModelState outputState = VariantModelState.EMPTY;

	@Override
	protected void createSyncedFields() {
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.Fluids.xpJuice);
		xpOutputs = new SyncableSides();
		itemOutputs = new SyncableSides();
		vacuumDisabled = new SyncableBoolean();
	}

	public TileEntityVacuumHopper() {
		super(OpenBlocks.TileEntities.vacuumHopper);
		sided.registerAllSlots(itemOutputs, false, true);

		itemHandlerCapability.registerAllSlots(itemOutputs, false, true);
	}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addSyncListener(itemHandlerCapability.createSyncListener());

		syncMap.addUpdateListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				if (changes.contains(xpOutputs) || changes.contains(itemOutputs)) {
					updateOutputStates();
					world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.RERENDER_MAIN_THREAD);
				}
			}

			private void updateOutputStates() {
				ImmutableMap.Builder<String, String> newOutputState = ImmutableMap.builder();
				for (Direction side : Direction.values()) {
					final boolean outputItems = itemOutputs.get(side);
					final boolean outputXp = xpOutputs.get(side);

					if (outputItems) {
						if (outputXp) {
							newOutputState.put(side.getString(), OUTPUT_BOTH);
						} else {
							newOutputState.put(side.getString(), OUTPUT_ITEMS);
						}
					} else if (outputXp) {
						newOutputState.put(side.getString(), OUTPUT_FLUIDS);
					}
				}

				outputState = VariantModelState.create(newOutputState.build());
			}
		});
	}

	public IReadableBitMap<Direction> getReadableXpOutputs() {
		return xpOutputs;
	}

	public IWriteableBitMap<Direction> getWriteableXpOutputs() {
		return BitMapUtils.createRpcAdapter(createRpcProxy(xpOutputs, IRpcDirectionBitMap.class));
	}

	public IReadableBitMap<Direction> getReadableItemOutputs() {
		return itemOutputs;
	}

	public IWriteableBitMap<Direction> getWriteableItemOutputs() {
		return BitMapUtils.createRpcAdapter(createRpcProxy(itemOutputs, IRpcDirectionBitMap.class));
	}

	public IValueProvider<FluidStack> getFluidProvider() {
		return tank;
	}

	private final Predicate<Entity> entitySelector = entity -> {
		if (!entity.isAlive()) {
			return false;
		}

		if (entity instanceof ProjectileEntity) {
			return entity.getMotion().y < 0.01;
		}

		if (entity instanceof ItemEntity) {
			ItemStack stack = ((ItemEntity)entity).getItem();
			return InventoryUtils.canInsertStack(inventory.getHandler(), stack);
		}

		if (entity instanceof ExperienceOrbEntity) {
			return tank.getSpace() > 0;
		}

		return false;
	};

	@Override
	public void tick() {
		if (vacuumDisabled.get()) {
			return;
		}

		if (world.isRemote) {
			spawnParticle(ParticleTypes.PORTAL, world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 1.0, world.rand.nextDouble() - 0.5);
		}

		List<Entity> interestingItems = world.getEntitiesWithinAABB(Entity.class, getBB().grow(3), entitySelector);

		boolean needsSync = false;

		for (Entity entity : interestingItems) {
			double dx = (pos.getX() + 0.5D - entity.getPosX());
			double dy = (pos.getY() + 0.5D - entity.getPosY());
			double dz = (pos.getZ() + 0.5D - entity.getPosZ());

			double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
			if (distance < 1.1) {
				needsSync |= onEntityCollidedWithBlock(entity);
			} else {
				double pull = 1.0 - distance / 15.0;

				if (pull > 0) {
					pull *= pull;
					Vector3d motion = entity.getMotion();
					entity.setMotion(
							motion.x + dx / distance * pull * 0.05,
							motion.y + dy / distance * pull * 0.2,
							motion.z + dz / distance * pull * 0.05
					);
				}
			}

		}

		if (!world.isRemote) {
			needsSync |= outputToNeighbors();
			if (needsSync) {
				sync();
			}
		}
	}

	private boolean outputToNeighbors() {
		if (world.getGameTime() % 10 == 0) {
			if (needsTankUpdate) {
				tank.updateNeighbours(world, pos);
				needsTankUpdate = false;
			}

			tank.distributeToSides(50, world, pos, xpOutputs.getValue());
			autoInventoryOutput();
			return true;
		}

		return false;
	}

	private void autoInventoryOutput() {
		final boolean outputSides = itemOutputs.getValue().isEmpty();
		if (outputSides) {
			return;
		}
		final ItemMover mover = new ItemMover(world, pos).breakAfterFirstTry().randomizeSides().setSides(itemOutputs.getValue());
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (!inventory.getStackInSlot(i).isEmpty()) {
				if (mover.pushFromSlot(inventory.getHandler(), i) > 0) {
					break;
				}
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isRemote && hand == Hand.MAIN_HAND && player.isSneaking()) {
			if (player.getHeldItemMainhand().isEmpty()) {
				vacuumDisabled.toggle();
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	public boolean onEntityCollidedWithBlock(Entity entity) {
		if (!world.isRemote) {
			if (entity instanceof ItemEntity && entity.isAlive()) {
				final ItemEntity item = (ItemEntity)entity;
				final ItemStack toConsume = item.getItem().copy();
				final ItemStack leftover = ItemHandlerHelper.insertItem(inventory.getHandler(), toConsume, false);
				ItemUtils.setEntityItemStack(item, leftover);
				return true;
			} else if (entity instanceof ExperienceOrbEntity) {
				if (tank.getSpace() > 0) {
					ExperienceOrbEntity orb = (ExperienceOrbEntity)entity;
					int xpAmount = FluidXpUtils.xpJuiceConverter.xpToFluid(orb.getXpValue());
					FluidStack newFluid = new FluidStack(OpenBlocks.Fluids.xpJuice, xpAmount);
					tank.fill(newFluid, IFluidHandler.FluidAction.EXECUTE);
					entity.remove(false);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ISidedInventory getInventory() {
		return sided;
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag = super.write(tag);
		inventory.writeToNBT(tag);
		return tag;
	}

	@Override
	public void read(final BlockState blockState, CompoundNBT tag) {
		super.read(blockState, tag);
		inventory.readFromNBT(tag);
	}

	@Override
	public void validate() {
		super.validate();
		this.needsTankUpdate = true;
	}

	@Override
	public void onNeighbourChanged(BlockPos neighbourPos, Block neighbourBlock) {
		this.needsTankUpdate = true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> tankCapability.getHandler(facing)).cast();
		}

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> itemHandlerCapability.getHandler(facing)).cast();
		}

		return super.getCapability(capability, facing);
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(VariantModelState.PROPERTY, () -> outputState).build();
	}

	@Override
	public ITextComponent getDisplayName() {
		return CONTAINER_NAME;
	}

	@Nullable
	@Override
	public Container createMenu(int windowId, PlayerInventory player, PlayerEntity entity) {
		return new ContainerVacuumHopper(player, windowId, this);
	}
}
