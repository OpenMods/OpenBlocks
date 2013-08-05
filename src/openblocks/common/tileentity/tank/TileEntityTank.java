package openblocks.common.tileentity.tank;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import openblocks.OpenBlocks;
import openblocks.api.IAwareTile;
import openblocks.common.tileentity.OpenTileEntity;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapTile;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableTank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class TileEntityTank extends TileEntityTankBase {

	/**
	 * This tank gets synced to the client
	 */
	private SyncableTank tank = new SyncableTank(LiquidContainerRegistry.BUCKET_VOLUME * 8);
	
	/**
	 * Not yet used
	 */
	private SyncableFlags flags = new SyncableFlags();
	
	/**
	 * Keys of things what get synced
	 */
	public enum Keys {
		tank,
		flags
	}

	/**
	 * This is used to give the water a bit of a.. wave
	 */
	private double flowTimer = Math.random() * 100;
	
	public TileEntityTank() {
		syncMap.put(Keys.tank, tank);
		syncMap.put(Keys.flags, flags);
	}
	
	public void updateEntity() {
		super.updateEntity();
		
		if (!worldObj.isRemote) { 
			
			HashSet<TileEntityTank> except = new HashSet<TileEntityTank>();
			except.add(this);
			
			// if we have a liquid
			if (tank.getLiquid() != null) {
				
				// try to fill up the tank below with as much liquid as possible
				TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
				if (below != null) {
					if (below.getSpace() > 0) {
						LiquidStack myLiquid = tank.getLiquid().copy();
						int toFill = Math.min(below.getSpace(), myLiquid.amount);
						myLiquid.amount = toFill;
						below.fill(myLiquid, true, except);
						tank.drain(toFill, true);
					}
				}
			}
			
			// now fill up the horizontal tanks, start with the least full
			ArrayList<TileEntityTank> horizontals = getHorizontalTanksOrdererdBySpace(except);
			for (TileEntityTank horizontal : horizontals) {
				LiquidStack liquid = tank.getLiquid();
				int difference = getAmount() - horizontal.getAmount();
				if (difference > 1) {
					int halfDifference = difference / 2;
					LiquidStack liquidCopy = liquid.copy();
					liquidCopy.amount = Math.min(500, halfDifference);
					int filled = horizontal.fill(liquidCopy, true, except);
					tank.drain(filled, true);
				}
			}
			syncMap.sync(worldObj, this, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
		} else {
			flowTimer += 0.1;
		}
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
			if (sideTank != null) {
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
		if (resource.amount > 0) {
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
		return null;
	}


	@Override
	public void onSynced(List<ISyncableObject> changes) {
		//System.out.println(tank.getAmount());
	}
	
	@Override
	public void onBlockBroken() {
		invalidate();
	}
}
