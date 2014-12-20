package openblocks.common.tileentity;

import java.util.Random;

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
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.legacy.ItemDistribution;
import openmods.liquids.GenericFluidHandler;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntitySprinkler extends SyncedTileEntity implements IBreakAwareTile, ISurfaceAttachment, IInventoryProvider, IExtendable, IHasGui {

	private static final FluidStack WATER = new FluidStack(FluidRegistry.WATER, 1);
	private static final ItemStack BONEMEAL = new ItemStack(Items.dye, 1, 15);

	private static final Random RANDOM = new Random();

	private boolean hasBonemeal = false;

	public enum Flags {
		enabled
	}

	private SyncableFlags flags;
	private SyncableTank tank;

	public int ticks;

	private final GenericInventory inventory = registerInventoryCallback(new GenericInventory("sprinkler", true, 9) {
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
		tank = new SyncableTank(FluidContainerRegistry.BUCKET_VOLUME, WATER, OpenBlocks.XP_FLUID);
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

	private void sprayParticles() {
		if (tank.getFluidAmount() > 0) {
			for (int sprinklerOutletIndex = 0; sprinklerOutletIndex < 6; sprinklerOutletIndex++) {
				float outletPosition = (sprinklerOutletIndex - 2.5f) / 5f;
				ForgeDirection blockYawRotation = getRotation();

				float sprayDirection = getSprayDirection();

				double sprayRoll = Math.sin(Math.toRadians(sprayDirection * 30f));
				double sprayPitchScatter = Math.sin(Math.toRadians(6 * (RANDOM.nextDouble() - 0.5)));
				Vec3 vec = Vec3.createVectorHelper(
						sprayRoll * blockYawRotation.offsetZ / -2f
								+ sprayPitchScatter * blockYawRotation.offsetX,
						0.4f,
						sprayRoll * blockYawRotation.offsetX / 2f
								+ sprayPitchScatter * blockYawRotation.offsetZ);

				OpenBlocks.proxy.spawnLiquidSpray(worldObj, tank.getFluid(),
						xCoord + 0.5 + (outletPosition * 0.6 * blockYawRotation.offsetX),
						yCoord + 0.2,
						zCoord + 0.5 + (outletPosition * 0.6 * blockYawRotation.offsetZ),
						0.3f, 0.7f, vec);
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
