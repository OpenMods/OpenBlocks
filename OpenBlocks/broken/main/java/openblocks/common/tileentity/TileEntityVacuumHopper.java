package openblocks.common.tileentity;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiVacuumHopper;
import openblocks.common.FluidXpUtils;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.entity.EntityItemProjectile;
import openmods.OpenMods;
import openmods.api.IActivateAwareTile;
import openmods.api.IHasGui;
import openmods.api.INeighbourAwareTile;
import openmods.api.IValueProvider;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.inventory.GenericInventory;
import openmods.inventory.ISidedInventoryDelegate;
import openmods.inventory.ItemMover;
import openmods.inventory.TileEntityInventory;
import openmods.liquids.SidedFluidCapabilityWrapper;
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

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityVacuumHopper extends SyncedTileEntity implements ISidedInventoryDelegate, IActivateAwareTile, IHasGui, INeighbourAwareTile, ITickable {

	public static final int TANK_CAPACITY = FluidXpUtils.xpJuiceConverter.xpToFluid(EnchantmentUtils.getExperienceForLevel(5));

	public static final String OUTPUT_ITEMS = "items";
	public static final String OUTPUT_FLUIDS = "fluids";
	public static final String OUTPUT_BOTH = "both";

	private SyncableTank tank;
	public SyncableSides xpOutputs;
	public SyncableSides itemOutputs;
	public SyncableBoolean vacuumDisabled;

	private boolean needsTankUpdate;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "vacuumhopper", true, 10));

	private final SidedInventoryAdapter sided = new SidedInventoryAdapter(inventory);

	private final SidedItemHandlerAdapter itemHandlerCapability = new SidedItemHandlerAdapter(inventory.getHandler());

	private final SidedFluidCapabilityWrapper tankCapability = SidedFluidCapabilityWrapper.wrap(tank, xpOutputs, true, false);

	private Map<String, String> outputState = ImmutableMap.of();

	@Override
	protected void createSyncedFields() {
		tank = new SyncableTank(TANK_CAPACITY, OpenBlocks.Fluids.xpJuice);
		xpOutputs = new SyncableSides();
		itemOutputs = new SyncableSides();
		vacuumDisabled = new SyncableBoolean();
	}

	public TileEntityVacuumHopper() {
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
					world.markBlockRangeForRenderUpdate(pos, pos);
				}
			}

			private void updateOutputStates() {
				ImmutableMap.Builder<String, String> newOutputState = ImmutableMap.builder();
				for (Direction side : Direction.VALUES) {
					final boolean outputItems = itemOutputs.get(side);
					final boolean outputXp = xpOutputs.get(side);

					if (outputItems) {
						if (outputXp) {
							newOutputState.put(side.getName(), OUTPUT_BOTH);
						} else {
							newOutputState.put(side.getName(), OUTPUT_ITEMS);
						}
					} else if (outputXp) {
						newOutputState.put(side.getName(), OUTPUT_FLUIDS);
					}
				}

				outputState = newOutputState.build();
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
		if (entity.isDead) return false;

		if (entity instanceof EntityItemProjectile) return entity.motionY < 0.01;

		if (entity instanceof ItemEntity) {
			ItemStack stack = ((ItemEntity)entity).getItem();
			return InventoryUtils.canInsertStack(inventory.getHandler(), stack);
		}

		if (entity instanceof ExperienceOrbEntity) return tank.getSpace() > 0;

		return false;
	};

	@Override
	public void update() {

		if (vacuumDisabled.get()) return;

		if (world.isRemote) {
			spawnParticle(EnumParticleTypes.PORTAL, world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 1.0, world.rand.nextDouble() - 0.5);
		}

		List<Entity> interestingItems = world.getEntitiesWithinAABB(Entity.class, getBB().grow(3), entitySelector);

		boolean needsSync = false;

		for (Entity entity : interestingItems) {
			double dx = (pos.getX() + 0.5D - entity.posX);
			double dy = (pos.getY() + 0.5D - entity.posY);
			double dz = (pos.getZ() + 0.5D - entity.posZ);

			double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
			if (distance < 1.1) {
				needsSync |= onEntityCollidedWithBlock(entity);
			} else {
				double var11 = 1.0 - distance / 15.0;

				if (var11 > 0.0D) {
					var11 *= var11;
					entity.motionX += dx / distance * var11 * 0.05;
					entity.motionY += dy / distance * var11 * 0.2;
					entity.motionZ += dz / distance * var11 * 0.05;
				}
			}

		}

		if (!world.isRemote) {
			needsSync |= outputToNeighbors();
			if (needsSync) sync();
		}
	}

	private boolean outputToNeighbors() {
		if (OpenMods.proxy.getTicks(world) % 10 == 0) {
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
		if (outputSides) return;
		final ItemMover mover = new ItemMover(world, pos).breakAfterFirstTry().randomizeSides().setSides(itemOutputs.getValue());
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (!inventory.getStackInSlot(i).isEmpty()) {
				if (mover.pushFromSlot(inventory.getHandler(), i) > 0) break;
			}
		}
	}

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerVacuumHopper(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiVacuumHopper(new ContainerVacuumHopper(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean onBlockActivated(PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && hand == Hand.MAIN_HAND && player.isSneaking()) {
			if (player.getHeldItemMainhand().isEmpty()) {
				vacuumDisabled.toggle();
				return true;
			}
		}
		return false;
	}

	public boolean onEntityCollidedWithBlock(Entity entity) {
		if (!world.isRemote) {
			if (entity instanceof ItemEntity && !entity.isDead) {
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
					tank.fill(newFluid, true);
					entity.setDead();
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
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		return tag;
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
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

	public Map<String, String> getOutputState() {
		return outputState;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return itemHandlerCapability.hasHandler(facing);

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return tankCapability.hasHandler(facing);

		return super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T)tankCapability.getHandler(facing);

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)itemHandlerCapability.getHandler(facing);

		return super.getCapability(capability, facing);
	}

}
