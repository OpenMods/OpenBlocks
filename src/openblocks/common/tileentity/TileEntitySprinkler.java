package openblocks.common.tileentity;

import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiSprinkler;
import openblocks.common.container.ContainerSprinkler;
import openmods.*;
import openmods.api.IBreakAwareTile;
import openmods.api.IHasGui;
import openmods.api.ISurfaceAttachment;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.liquids.GenericFluidHandler;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableFlags;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;
import openmods.utils.InventoryUtils;

public class TileEntitySprinkler extends SyncedTileEntity implements IBreakAwareTile, ISurfaceAttachment, IInventoryProvider, IExtendable, IHasGui {

	private static final FluidStack WATER = new FluidStack(FluidRegistry.WATER, 1);
	private static final ItemStack BONEMEAL = new ItemStack(Item.dyePowder, 1, 15);

	private boolean hasBonemeal = false;

	public enum Flags {
		enabled
	}

	private SyncableFlags flags;
	private SyncableTank tank;

	private final GenericInventory inventory = new GenericInventory("sprinkler", true, 9) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return itemstack != null && itemstack.isItemEqual(BONEMEAL);
		}
	};

	@IncludeInterface
	private final IFluidHandler tankWrapper = new GenericFluidHandler.Drain(tank);

	@Override
	protected void createSyncedFields() {
		flags = new SyncableFlags();
		tank = new SyncableTank(FluidContainerRegistry.BUCKET_VOLUME, WATER, OpenBlocks.XP_FLUID);
	}

	private void attemptFertilize() {
		if (worldObj == null || worldObj.isRemote) return;
		if (worldObj.rand.nextDouble() < 1.0 / (hasBonemeal? Config.sprinklerBonemealFertizizeChance : Config.sprinklerFertilizeChance)) {
			// http://goo.gl/RpQuk9
			Random random = worldObj.rand;
			int x = (random.nextInt(Config.sprinklerEffectiveRange + 1))
					* (random.nextBoolean()? 1 : -1) + xCoord;
			int z = (random.nextInt(Config.sprinklerEffectiveRange + 1))
					* (random.nextBoolean()? 1 : -1) + zCoord;
			/*
			 * What? Okay think about this. i = -1 y = yCoord - 1 i = 0 y =
			 * yCoord - 1 i = 1 y = yCoord
			 * 
			 * Is this the intended operation? I've changed it for now -NC
			 */
			for (int i = -1; i <= 1; i++) {
				int y = yCoord + i;
				try {
					for (int a = 0; a < 10; a++) {
						// Mikee, why do we try to apply it 10 times? Is it
						// likely to fail? -NC
						if (ItemDye.applyBonemeal(BONEMEAL.copy(), worldObj, x, y, z, new FakePlayer(worldObj, "sprinkler"))) {
							break;
						}
					}
				} catch (Exception e) {
					Log.warn(e, "Exception during bonemeal applying");
				}
			}
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
		if (worldObj == null || !worldObj.isRemote) return;
		if (tank.getFluidAmount() > 0) {
			for (int i = 0; i < 6; i++) {
				float offset = (i - 2.5f) / 5f;
				ForgeDirection rotation = getRotation();
				OpenBlocks.proxy.spawnLiquidSpray(worldObj, tank.getFluid(), xCoord + 0.5
						+ (offset * 0.6 * rotation.offsetX), yCoord, zCoord + 0.5
						+ (offset * 0.6 * rotation.offsetZ), rotation, getSprayPitch(), 2 * offset);
			}
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

			tank.autoFillFromSides(OpenMods.proxy, 3, this);

			// every 60 ticks drain from the tank
			// if there's nothing to drain, disable it
			final long ticks = OpenMods.proxy.getTicks(worldObj);

			if (ticks % 1200 == 0) {
				hasBonemeal = InventoryUtils.consumeInventoryItem(inventory, BONEMEAL);
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
			attemptFertilize();
			sprayParticles();
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

	public float getSprayPitch() {
		return (float)(getSprayAngle() * Math.PI);
	}

	public float getSprayAngle() {
		if (isEnabled()) { return MathHelper.sin(OpenMods.proxy.getTicks(worldObj) * 0.02f) * (float)Math.PI * 0.035f; }
		return 0;
	}

	@IncludeOverride
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

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
