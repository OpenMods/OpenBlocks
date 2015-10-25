package openblocks.common.tileentity;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiSprinkler;
import openblocks.common.container.ContainerSprinkler;
import openmods.api.IBreakAwareTile;
import openmods.api.IHasGui;
import openmods.api.ISurfaceAttachment;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fakeplayer.FakePlayerPool.PlayerUser;
import openmods.fakeplayer.OpenModsFakePlayer;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.inventory.legacy.ItemDistribution;
import openmods.liquids.GenericFluidHandler;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntitySprinkler extends SyncedTileEntity implements IBreakAwareTile, ISurfaceAttachment, IInventoryProvider, IHasGui {

	private static final ItemStack BONEMEAL = new ItemStack(Items.dye, 1, 15);

	private static final Random RANDOM = new Random();

	private static final double[] SPRINKER_DELTA = new double[] { 0.2, 0.25, 0.5 };
	private static final int[] SPRINKER_MOD = new int[] { 1, 5, 20 };

	private boolean hasBonemeal = false;

	public enum Flags {
		enabled
	}

	private SyncableFlags flags;
	private SyncableTank tank;

	public int ticks;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "sprinkler", true, 9) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return itemstack != null && itemstack.isItemEqual(BONEMEAL);
		}
	});

	@IncludeInterface
	private final IFluidHandler tankWrapper = new GenericFluidHandler.Drain(tank);

	@Override
	protected void createSyncedFields() {
		flags = SyncableFlags.create(Flags.values().length);
		tank = new SyncableTank(FluidContainerRegistry.BUCKET_VOLUME, FluidRegistry.WATER, OpenBlocks.Fluids.xpJuice);
	}

	private static int selectFromRange(int range) {
		return RANDOM.nextInt(2 * range + 1) - range;
	}

	private void attemptFertilize() {
		if (!(worldObj instanceof WorldServer)) return;
		final int fertilizerChance = hasBonemeal? Config.sprinklerBonemealFertizizeChance : Config.sprinklerFertilizeChance;
		if (RANDOM.nextDouble() < 1.0 / fertilizerChance) {
			FakePlayerPool.instance.executeOnPlayer((WorldServer)worldObj, new PlayerUser() {
				@Override
				public void usePlayer(OpenModsFakePlayer fakePlayer) {
					final int x = selectFromRange(Config.sprinklerEffectiveRange) + xCoord;
					final int z = selectFromRange(Config.sprinklerEffectiveRange) + zCoord;

					for (int i = -1; i <= 1; i++) {
						int y = yCoord + i;

						if (ItemDye.applyBonemeal(BONEMEAL.copy(), worldObj, x, y, z, fakePlayer))
						break;

					}
				}
			});
		}
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerSprinkler(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiSprinkler(new ContainerSprinkler(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	private static final double SPRAY_SIDE_SCATTER = Math.toRadians(25);

	private void sprayParticles() {
		if (tank.getFluidAmount() > 0) {
			// 0 = All, 1 = Decreased, 2 = Minimal
			final int particleSetting = Minecraft.getMinecraft().gameSettings.particleSetting;
			if (particleSetting > 2) return;

			final int fillFactor = SPRINKER_MOD[particleSetting];

			if ((ticks % fillFactor) != 0) return;
			final ForgeDirection blockYawRotation = getOrientation().north();
			final double nozzleAngle = getSprayDirection();
			final double sprayForwardVelocity = Math.sin(Math.toRadians(nozzleAngle * 25));

			final double forwardVelocityX = sprayForwardVelocity * blockYawRotation.offsetZ / -2;
			final double forwardVelocityZ = sprayForwardVelocity * blockYawRotation.offsetX / 2;

			final double sprinklerDelta = SPRINKER_DELTA[particleSetting];
			double outletPosition = -0.5;

			while (outletPosition <= 0.5) {
				final double spraySideVelocity = Math.sin(SPRAY_SIDE_SCATTER * (RANDOM.nextDouble() - 0.5));

				final double sideVelocityX = spraySideVelocity * blockYawRotation.offsetX;
				final double sideVelocityZ = spraySideVelocity * blockYawRotation.offsetZ;

				Vec3 vec = Vec3.createVectorHelper(
						forwardVelocityX + sideVelocityX,
						0.35,
						forwardVelocityZ + sideVelocityZ);

				OpenBlocks.proxy.spawnLiquidSpray(worldObj, tank.getFluid().getFluid(),
						xCoord + 0.5 + (outletPosition * 0.6 * blockYawRotation.offsetX),
						yCoord + 0.2,
						zCoord + 0.5 + (outletPosition * 0.6 * blockYawRotation.offsetZ),
						0.3f, 0.7f, vec);

				outletPosition += sprinklerDelta;
			}
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		ticks++;
		if (!worldObj.isRemote) {

			tank.fillFromSides(3, worldObj, getPosition());

			// every 60 ticks drain from the tank
			// if there's nothing to drain, disable it

			if (ticks % 1200 == 0) {
				hasBonemeal = ItemDistribution.consumeFirstInventoryItem(inventory, BONEMEAL);
			}
			if (ticks % 60 == 0) {
				setEnabled(tank.drain(1, true) != null);
				sync();
			}

			// if it's enabled..

		}
		// simplified this action because only one of these will execute
		// depending on worldObj.isRemote
		if (isEnabled()) {
			if (worldObj.isRemote) sprayParticles();
			else attemptFertilize();
		}
	}

	private void setEnabled(boolean b) {
		flags.set(Flags.enabled, b);
	}

	private boolean isEnabled() {
		return flags.get(Flags.enabled);
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void onBlockBroken() {
		if (!worldObj.isRemote && !worldObj.isAirBlock(xCoord, yCoord, zCoord)) {
			BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, new ItemStack(OpenBlocks.Blocks.sprinkler));
		}
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

	@IncludeOverride
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	@IncludeInterface
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}
}
