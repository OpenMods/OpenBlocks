package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
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
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.common.api.IAwareTile;
import openblocks.common.api.ISurfaceAttachment;
import openblocks.utils.BlockUtils;
import openblocks.utils.InventoryUtils;

public class TileEntitySprinkler extends OpenTileEntity implements IAwareTile,
		ISurfaceAttachment, ITankContainer, IInventory {

	private LiquidStack water = new LiquidStack(Block.waterStill, 1);

	private ItemStack bonemeal = new ItemStack(Item.dyePowder, 1, 15);

	private LiquidTank tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);

	private GenericInventory inventory = new GenericInventory("sprinkler", true, 9);
	
	private boolean hasBonemeal = false;

	private void attemptFertilize() {
		if (worldObj == null || worldObj.isRemote) return;
		if (worldObj.rand.nextDouble() < 1.0 / (hasBonemeal ? OpenBlocks.Config.sprinklerBonemealFertizizeChance : OpenBlocks.Config.sprinklerFertilizeChance)) {
			int x = xCoord + worldObj.rand.nextInt(9) - 4;	// This isn't balanced http://goo.gl/RpQuk9 -NC
			int z = zCoord + worldObj.rand.nextInt(9) - 4;  // -4, not -5
			/* What? Okay think about this.
			 * i = -1 y = yCoord - 1
			 * i = 0 y = yCoord - 1
			 * i = 1 y = yCoord
			 * 
			 * Is this the intended operation? I've changed it for now -NC
			 */
			for (int i = -1; i <= 1; i++) {
				int y = yCoord + i;
				for (int a = 0; a < 10; a++) {
					// Mikee, why do we try to apply it 10 times? Is it likely to fail? -NC
					if (ItemDye.applyBonemeal(bonemeal.copy(), worldObj, x, y, z, new FakePlayer(worldObj, "sprinkler"))) {
						break;
					}
				}
			}
		}
	}

	private void sprayParticles() {
		if (worldObj == null || !worldObj.isRemote) return;
		for (int i = 0; i < 6; i++) {
			float offset = (i - 2.5f) / 5f;
			ForgeDirection rotation = getRotation();
			OpenBlocks.proxy.spawnLiquidSpray(worldObj, water, xCoord + 0.5
					+ (offset * 0.6 * rotation.offsetX), yCoord, zCoord + 0.5
					+ (offset * 0.6 * rotation.offsetZ), rotation, getSprayPitch(), 2 * offset);
		}
	}
	
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

			if (tank.getLiquid() == null || tank.getLiquid().amount == 0) {
				TileEntity below = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
				if (below instanceof ITankContainer) {
					ITankContainer belowTank = (ITankContainer)below;
					LiquidStack drained = belowTank.drain(ForgeDirection.UP, tank.getCapacity(), false);
					if (drained != null && drained.isLiquidEqual(water)) {
						drained = belowTank.drain(ForgeDirection.UP, tank.getCapacity(), true);
						if (drained != null) {
							tank.fill(drained, true);
						}
					}
				}
			}

			// every 60 ticks drain from the tank
			// if there's nothing to drain, disable it
			if (worldObj.getTotalWorldTime() % 1200 == 0) {
				hasBonemeal = InventoryUtils.consumeInventoryItem(inventory, bonemeal);
			}
			if (worldObj.getTotalWorldTime() % 60 == 0) {
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
	public void openChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return inventory.isStackValidForSlot(i, itemstack);
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		if (resource != null && resource.isLiquidEqual(water)) { return tank.fill(resource, doFill); }
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return fill(ForgeDirection.UNKNOWN, resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[] { tank };
	}
	
	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return tank;
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player, OpenBlocks.Gui.Sprinkler);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		setRotation(BlockUtils.get2dOrientation(player));
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}

	public float getSprayPitch() {
		return (float)(getSprayAngle() * Math.PI);
	}

	public float getSprayAngle() {
		if (isEnabled()) {
			float angle = (float)(MathHelper.sin(worldObj.getTotalWorldTime() * 0.01f)
					* Math.PI * 0.1f);
			return (float)(angle);
		}
		return 0;
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
		if (tag.hasKey("rotation")) {
			byte ordinal = tag.getByte("rotation");
			setRotation(ForgeDirection.getOrientation(ordinal));
			sync();
		}
	}

}
