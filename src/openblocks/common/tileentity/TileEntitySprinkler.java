package openblocks.common.tileentity;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import openblocks.Config;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.common.api.IAwareTile;
import openblocks.common.api.ISurfaceAttachment;
import openblocks.utils.BlockUtils;
import openblocks.utils.InventoryUtils;

public class TileEntitySprinkler extends OpenTileEntity implements IAwareTile, ISurfaceAttachment, IFluidHandler, IInventory {

	// erpppppp
	private FluidStack water = new FluidStack(FluidRegistry.WATER, 1);

	private ItemStack bonemeal = new ItemStack(Item.dyePowder, 1, 15);

	private FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);

	private GenericInventory inventory = new GenericInventory("sprinkler", true, 9);

	private boolean hasBonemeal = false;

	private void attemptFertilize() {
		if (worldObj == null || worldObj.isRemote) return;
		if (worldObj.rand.nextDouble() < 1.0 / (hasBonemeal? Config.sprinklerBonemealFertizizeChance : Config.sprinklerFertilizeChance)) {
			// http://goo.gl/RpQuk9
			Random random = worldObj.rand;
			int x = (random.nextInt(Config.sprinklerEffectiveRange) + 1) * (random.nextBoolean()? 1 : -1) + xCoord;
			int z = (random.nextInt(Config.sprinklerEffectiveRange) + 1) * (random.nextBoolean()? 1 : -1) + zCoord;
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
						if (ItemDye.applyBonemeal(bonemeal.copy(), worldObj, x, y, z, new FakePlayer(worldObj, "sprinkler"))) {
							break;
						}
					}
				} catch (Exception e) {
					Log.warn(e, "Exception during bonemeal applying");
				}
			}
		}
	}

	private void sprayParticles() {
		if (worldObj == null || !worldObj.isRemote) return;
		for (int i = 0; i < 6; i++) {
			float offset = (i - 2.5f) / 5f;
			ForgeDirection rotation = getRotation();
			OpenBlocks.proxy.spawnLiquidSpray(worldObj, water, xCoord + 0.5 + (offset * 0.6 * rotation.offsetX), yCoord, zCoord + 0.5 + (offset * 0.6 * rotation.offsetZ), rotation, getSprayPitch(), 2 * offset);
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (tank.getFluid() == null || tank.getFluid().amount == 0) {
				TileEntity below = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
				if (below instanceof IFluidHandler) {

					IFluidHandler belowTank = (IFluidHandler)below;
					FluidStack drained = belowTank.drain(ForgeDirection.UP, tank.getCapacity(), false);
					if (drained != null && drained.isFluidEqual(water)) {
						drained = belowTank.drain(ForgeDirection.UP, tank.getCapacity(), true);
						if (drained != null) {
							tank.fill(drained, true);
						}
					}
				}
			}

			// every 60 ticks drain from the tank
			// if there's nothing to drain, disable it
			if (OpenBlocks.proxy.getTicks(worldObj) % 1200 == 0) {
				hasBonemeal = InventoryUtils.consumeInventoryItem(inventory, bonemeal);
			}
			if (OpenBlocks.proxy.getTicks(worldObj) % 60 == 0) {
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
		setFlag1(b);
	}

	private boolean isEnabled() {
		return getFlag1();
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack != null && itemstack.isItemEqual(bonemeal);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource != null && resource.isFluidEqual(water)) { return tank.fill(resource, doFill); }
		return 0;
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

	@Override
	public void onBlockAdded() {}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player, OpenBlocks.Gui.Sprinkler);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		setRotation(BlockUtils.get2dOrientation(player));
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		return false;
	}

	public float getSprayPitch() {
		return (float)(getSprayAngle() * Math.PI);
	}

	public float getSprayAngle() {
		if (isEnabled()) { return MathHelper.sin(OpenBlocks.proxy.getTicks(worldObj) * 0.02f) * (float)Math.PI * 0.035f; }
		return 0;
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
		if (tag.hasKey("rotation")) {
			byte ordinal = tag.getByte("rotation");
			setRotation(ForgeDirection.getOrientation(ordinal));
			sync();
		}
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

}
