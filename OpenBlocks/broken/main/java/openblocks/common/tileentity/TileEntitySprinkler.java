package openblocks.common.tileentity;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiSprinkler;
import openblocks.common.container.ContainerSprinkler;
import openmods.api.INeighbourAwareTile;
import openmods.api.ISurfaceAttachment;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryDelegate;
import openmods.inventory.TileEntityInventory;
import openmods.liquids.GenericFluidCapabilityWrapper;
import openmods.model.eval.EvalModelState;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntitySprinkler extends SyncedTileEntity implements ISurfaceAttachment, IInventoryDelegate, IHasGui, ITickable, INeighbourAwareTile {

	private static final ItemStack BONEMEAL = new ItemStack(Items.DYE, 1, 15);

	private static final Random RANDOM = new Random();

	private static final double[] SPRINKER_DELTA = new double[] { 0.2, 0.25, 0.5 };
	private static final int[] SPRINKER_MOD = new int[] { 1, 5, 20 };

	private boolean hasBonemeal = false;

	private boolean needsTankUpdate;

	public enum Flags {
		enabled
	}

	private SyncableFlags flags;
	private SyncableTank tank;

	public int ticks;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "sprinkler", true, 9) {
		@Override
		public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
			return !itemstack.isEmpty() && itemstack.isItemEqual(BONEMEAL);
		}
	});

	private final IFluidHandler tankWrapper = new GenericFluidCapabilityWrapper.Drain(tank);

	@Override
	protected void createSyncedFields() {
		flags = SyncableFlags.create(Flags.values().length);
		tank = new SyncableTank(Config.sprinklerInternalTank, FluidRegistry.WATER);
	}

	private static int selectFromRange(int range) {
		return RANDOM.nextInt(2 * range + 1) - range;
	}

	private void attemptFertilize() {
		if (!(world instanceof ServerWorld)) return;
		final int fertilizerChance = hasBonemeal? Config.sprinklerBonemealFertizizeChance : Config.sprinklerFertilizeChance;
		if (RANDOM.nextDouble() < 1.0 / fertilizerChance) {
			FakePlayerPool.instance.executeOnPlayer((ServerWorld)world, fakePlayer -> {
				final int x = selectFromRange(Config.sprinklerEffectiveRange);
				final int z = selectFromRange(Config.sprinklerEffectiveRange);

				for (int y = -1; y <= 1; y++) {
					BlockPos target = pos.add(x, y, z);

					if (DyeItem.applyBonemeal(BONEMEAL.copy(), world, target, fakePlayer, Hand.MAIN_HAND))
						break;

				}
			});
		}
	}

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerSprinkler(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiSprinkler(new ContainerSprinkler(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return true;
	}

	private static final double SPRAY_SIDE_SCATTER = Math.toRadians(25);

	private void sprayParticles() {
		if (tank.getFluidAmount() > 0) {
			// 0 = All, 1 = Decreased, 2 = Minimal
			final int particleSetting = OpenBlocks.proxy.getParticleSettings();
			if (particleSetting > 2) return;

			final int fillFactor = SPRINKER_MOD[particleSetting];

			if ((ticks % fillFactor) != 0) return;
			final Direction blockYawRotation = getOrientation().north();
			final double nozzleAngle = getSprayDirection();
			final double sprayForwardVelocity = Math.sin(Math.toRadians(nozzleAngle * 25));

			final int offsetZ = blockYawRotation.getFrontOffsetZ();
			final int offsetX = blockYawRotation.getFrontOffsetX();

			final double forwardVelocityX = sprayForwardVelocity * offsetZ / -2;
			final double forwardVelocityZ = sprayForwardVelocity * offsetX / 2;

			final double sprinklerDelta = SPRINKER_DELTA[particleSetting];
			double outletPosition = -0.5;

			while (outletPosition <= 0.5) {
				final double spraySideVelocity = Math.sin(SPRAY_SIDE_SCATTER * (RANDOM.nextDouble() - 0.5));

				final double sideVelocityX = spraySideVelocity * offsetX;
				final double sideVelocityZ = spraySideVelocity * offsetZ;

				Vec3d vec = new Vec3d(
						forwardVelocityX + sideVelocityX,
						0.35,
						forwardVelocityZ + sideVelocityZ);

				OpenBlocks.proxy.spawnLiquidSpray(world, tank.getFluid(),
						pos.getX() + 0.5 + (outletPosition * 0.6 * offsetX),
						pos.getY() + 0.2,
						pos.getZ() + 0.5 + (outletPosition * 0.6 * offsetZ),
						0.3f, 0.7f, vec);

				outletPosition += sprinklerDelta;
			}
		}
	}

	@Override
	public void update() {
		if (!world.isRemote) {

			if (tank.getFluidAmount() <= 0) {
				if (needsTankUpdate) {
					tank.updateNeighbours(world, pos);
					needsTankUpdate = false;
				}

				tank.fillFromSide(world, pos, Direction.DOWN);
			}

			if (ticks % Config.sprinklerBonemealConsumeRate == 0) {
				hasBonemeal = consumeFirstInventoryItem();
			}

			if (ticks % Config.sprinklerWaterConsumeRate == 0) {
				setEnabled(tank.drain(1, true) != null);
				sync();
			}
		}

		ticks++;

		// simplified this action because only one of these will execute
		// depending on world.isRemote
		if (isEnabled()) {
			if (world.isRemote) sprayParticles();
			else attemptFertilize();
		}
	}

	private boolean consumeFirstInventoryItem() {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack contents = inventory.getStackInSlot(i);
			if (!contents.isEmpty() && BONEMEAL.isItemEqual(contents)) {
				contents.shrink(1);
				inventory.setInventorySlotContents(i, contents);
				return true;
			}
		}
		return false;
	}

	private void setEnabled(boolean b) {
		flags.set(Flags.enabled, b);
	}

	private boolean isEnabled() {
		return flags.get(Flags.enabled);
	}

	@Override
	public Direction getSurfaceDirection() {
		return Direction.DOWN;
	}

	/**
	 * Get spray direction of Sprinkler particles
	 *
	 * @return float from -1f to 1f indicating the direction, left to right of the particles
	 */
	public float getSprayDirection() {
		if (isEnabled()) { return MathHelper.sin(ticks * 0.02f); }
		return 0;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		super.writeToNBT(tag);
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

	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
				capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
				super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T)tankWrapper;

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory.getHandler();

		return super.getCapability(capability, facing);
	}

	public EvalModelState getRenderState() {
		return EvalModelState.create().withArg("direction", getSprayDirection());
	}

	@Override
	public boolean hasFastRenderer() {
		return true;
	}
}
