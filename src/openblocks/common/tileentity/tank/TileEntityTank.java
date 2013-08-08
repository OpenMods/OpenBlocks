package openblocks.common.tileentity.tank;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import openblocks.OpenBlocks;
import openblocks.api.IAwareTile;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.common.tileentity.OpenTileEntity;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapTile;
import openblocks.sync.SyncableDirection;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableInt;
import openblocks.sync.SyncableTank;
import openblocks.utils.BlockUtils;
import openblocks.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.ITankContainer;

public class TileEntityTank extends TileEntityTankBase implements ITankContainer {

	/**
	 * This tank gets synced to the client
	 */
	private SyncableTank tank = new SyncableTank(LiquidContainerRegistry.BUCKET_VOLUME * OpenBlocks.Config.bucketsPerTank);
	
	/**
	 * The direction the tank is being filled from
	 */
	private SyncableDirection fillDirection = new SyncableDirection();
	
	/**
	 * A collection is booleans that get packed and synced
	 */
	private SyncableFlags flags = new SyncableFlags();

	/**
	 * The Id of the last liquid filled
	 */
	private SyncableInt lastLiquidId = new SyncableInt();
	
	/**
	 * The meta of the last liquid filled
	 */
	private SyncableInt lastLiquidMeta = new SyncableInt();
	
	private int ticksSinceFill = 1000;
	
	/**
	 * Keys of things what get synced
	 */
	public enum Keys {
		tank,
		flags,
		fillDirection,
		lastLiquidId,
		lastLiquidMeta
	}
	
	public enum Flags {
		isFilling
	}

	/**
	 * This is used to give the water a bit of a.. wave
	 */
	private double flowTimer = Math.random() * 100;
	
	public TileEntityTank() {
		syncMap.put(Keys.tank, tank);
		syncMap.put(Keys.flags, flags);
		syncMap.put(Keys.fillDirection, fillDirection);
		syncMap.put(Keys.lastLiquidId, lastLiquidId);
		syncMap.put(Keys.lastLiquidMeta, lastLiquidMeta);
	}
	
	public void updateEntity() {
		super.updateEntity();
		
		if (!worldObj.isRemote) { 

			if (ticksSinceFill < 5) {
				ticksSinceFill++;
			}
			flags.set(Flags.isFilling, ticksSinceFill < 5);
			
			HashSet<TileEntityTank> except = new HashSet<TileEntityTank>();
			except.add(this);
			
			// if we have a liquid
			if (tank.getLiquid() != null) {
				
				// try to fill up the tank below with as much liquid as possible
				TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
				if (below != null) {
					if (below.getSpace() > 0) {
						LiquidStack myLiquid = tank.getLiquid().copy();
						if (below.canReceiveLiquid(myLiquid)) {
							int toFill = Math.min(below.getSpace(), myLiquid.amount);
							myLiquid.amount = toFill;
							below.fill(myLiquid, true, except);
							tank.drain(toFill, true);
						}
					}
				}
			}
			
			// now fill up the horizontal tanks, start with the least full
			ArrayList<TileEntityTank> horizontals = getHorizontalTanksOrdererdBySpace(except);
			for (TileEntityTank horizontal : horizontals) {
				LiquidStack liquid = tank.getLiquid();
				if (horizontal.canReceiveLiquid(liquid)) {
					int difference = getAmount() - horizontal.getAmount();
					if (difference > 1) {
						int halfDifference = difference / 2;
						LiquidStack liquidCopy = liquid.copy();
						liquidCopy.amount = Math.min(500, halfDifference);
						int filled = horizontal.fill(liquidCopy, true, except);
						tank.drain(filled, true);
					}
				}
			}
			
			syncMap.sync(worldObj, this, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
		} else {
			if (flags.get(Flags.isFilling)) {
				//FXLiquidSpray fx = new FXLiquidSpray(worldObj, new LiquidStack(lastLiquidId.getValue(), 1, lastLiquidMeta.getValue()), countDownwardsTanks(), xCoord + 0.5, yCoord + 1, zCoord + 0.5, 1.5F, 0xFF0000, 6);
				//fx.noClip = true;
				//Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			flowTimer += 0.1;
		}
	}
	
	public boolean canReceiveLiquid(LiquidStack liquid) {
		if (!tank.containsValidLiquid()) {
			return true;
		}
		if (liquid == null) {
			return true;
		}
		LiquidStack otherLiquid = tank.getLiquid();
		if (otherLiquid != null) {
			return otherLiquid.isLiquidEqual(liquid);
		}
		return true;
	}
	
	public SyncableTank getInternalTank() {
		return tank;
	}
	
	public int getSpace() {
		return tank.getSpace();
	}

	public int getAmount() {
		return tank.getAmount();
	}
	
	/**
	 * Gives the liquid a wobble
	 * @return
	 */
	public double getFlowOffset() {
		return Math.sin(flowTimer) / 35;
	}
	
	/**
	 * Get the average liquid level on the sides of the current tank
	 */
	public double getLiquidHeightForSide(ForgeDirection ... sides) {
		double percentFull = tank.getPercentFull();
		if (percentFull > 0.98) {
			return 1.0;
		}
		double fullness = percentFull + getFlowOffset();
		int count = 1;
		for (ForgeDirection side : sides) {
			TileEntityTank sideTank = getTankInDirection(side);
			if (sideTank != null && sideTank.canReceiveLiquid(tank.getLiquid())) {
				fullness += sideTank.getInternalTank().getPercentFull() + sideTank.getFlowOffset();
				count++;
			}
		}
		return Math.max(0, Math.min(1, fullness / count));
	}
	
	public int fill(LiquidStack resource, boolean doFill, HashSet<TileEntityTank> except) {
		TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
		int filled = 0;
		if (except == null ) {
			except = new HashSet<TileEntityTank>();
		}
		int startAmount = resource.amount;
		if (except.contains(this)) {
			return 0;
		}
		except.add(this);
		
		// fill the tank below as much as possible
		if (below != null && below.getSpace() > 0) {
			filled = below.fill(resource, doFill, except);
			resource.amount -= filled;
		}
		
		// fill myself up
		if (resource.amount > 0){
			filled = tank.fill(resource, doFill);
			resource.amount -= filled;
		}
		
		// ok we cant, so lets fill the tank above
		if (resource.amount > 0) {
			TileEntityTank above = getTankInDirection(ForgeDirection.UP);
			if (above != null) {
				filled = above.fill(resource, doFill, except);
				resource.amount -= filled;
			}
		}
		
		// finally, distribute any remaining to the sides
		if (resource.amount > 0 && canReceiveLiquid(resource)) {
			ArrayList<TileEntityTank> horizontals = getHorizontalTanksOrdererdBySpace(except);
			if (horizontals.size() > 0) {
				int amountPerSide = resource.amount / horizontals.size();
				for (TileEntityTank sideTank : horizontals) {
					LiquidStack copy = resource.copy();
					copy.amount = amountPerSide;
					filled = sideTank.fill(copy, doFill, except);
					resource.amount -= filled;
				}
			}
		}
		return startAmount - resource.amount;
	}

	/**
	 * TODO
	 */
	public LiquidStack drain(int amount, boolean doDrain) {
		return tank.drain(amount, doDrain);
	}


	@Override
	public void onSynced(List<ISyncableObject> changes) {
		//System.out.println("synced");
	}
	
	@Override
	public void onBlockBroken() {
		//invalidate();
	}
	
	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		if (stack.hasTagCompound()) {
			tank.readFromNBT(stack.getTagCompound(), "tank");
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag, "tank");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag, "tank");
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		int filled = fill(resource, doFill, null);
		if (doFill && filled > 0) {
			fillDirection.setValue(from);
			ticksSinceFill = 0;
			if (resource != null) {
				lastLiquidId.setValue(resource.itemID);
				lastLiquidMeta.setValue(resource.itemMeta);
			}
		}
		return filled;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return fill(ForgeDirection.UNKNOWN, resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[] { tank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return tank;
	}
	
	public int countDownwardsTanks() {
		int count = 1;
		TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
		if (below != null){
			count += below.countDownwardsTanks();
		}
		return count;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

		ForgeDirection direction = BlockUtils.sideToDirection(side);
		
		ItemStack current = player.inventory.getCurrentItem();
		if (current != null) {

			LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(current);

			// Handle filled containers
			if (liquid != null) {
				int qty = fill(direction, liquid, true);

				if (qty != 0 && !player.capabilities.isCreativeMode) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
				}

				return true;
			} else {

				LiquidStack available = tank.getLiquid();
				if (available != null) {
					ItemStack filled = LiquidContainerRegistry.fillLiquidContainer(available, current);

					liquid = LiquidContainerRegistry.getLiquidForFilledItem(filled);

					if (liquid != null) {
						if (!player.capabilities.isCreativeMode) {
							if (current.stackSize > 1) {
								if (!player.inventory.addItemStackToInventory(filled))
									return false;
								else {
									player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
								}
							} else {
								player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
								player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
							}
						}
						drain(ForgeDirection.UNKNOWN, liquid.amount, true);
						return true;
					}
				}
			}
		}

		return false;
	}
}
