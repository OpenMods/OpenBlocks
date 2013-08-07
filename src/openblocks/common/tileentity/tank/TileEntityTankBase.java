package openblocks.common.tileentity.tank;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import openblocks.OpenBlocks;
import openblocks.api.IAwareTile;
import openblocks.common.tileentity.OpenTileEntity;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapTile;
import openblocks.sync.SyncableTank;
import openblocks.utils.BlockUtils;

public abstract class TileEntityTankBase extends OpenTileEntity implements ISyncHandler, IAwareTile {

	public HashMap<ForgeDirection, WeakReference<TileEntityTank>> neighbours = new HashMap<ForgeDirection, WeakReference<TileEntityTank>>();
	public HashMap<ForgeDirection, Boolean> surroundingBlocks = new HashMap<ForgeDirection, Boolean>();
	
	public static final ForgeDirection[] horizontalDirections = new ForgeDirection[] { 
		ForgeDirection.NORTH,
		ForgeDirection.SOUTH,
		ForgeDirection.EAST,
		ForgeDirection.WEST
	};

	protected Comparator<TileEntityTank> sortBySpace = new Comparator<TileEntityTank>() {
	    public int compare(TileEntityTank c1, TileEntityTank c2) {
	        return c2.getSpace() - c1.getSpace();
	    }
	};
	
	protected SyncMapTile syncMap = new SyncMapTile();
	
	public TileEntityTankBase() {
		
	}
	
	@Override
	public void updateEntity() {
		// if we call super, initialize() will fire
		super.updateEntity();
	}

	/**
	 * This method fires on the first tick. worldObj and coords will all be valid
	 */
	@Override
	protected void initialize() {
		findNeighbours();
	}
	
	/**
	 * Find the neighbouring tanks and store them in a hashmap
	 */
	protected void findNeighbours() {
		neighbours.clear();
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity neighbour = getTileInDirection(direction);
			if (neighbour != null && neighbour instanceof TileEntityTank) {
				neighbours.put(direction, new WeakReference<TileEntityTank>((TileEntityTank)neighbour));
			}
			surroundingBlocks.put(direction, !worldObj.isAirBlock(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ));
		}
		if (!worldObj.isRemote) {
			sendBlockEvent(0, 0);
		}
	}
	
	public boolean hasBlockOnSide(ForgeDirection side) {
		return surroundingBlocks.containsKey(side) && surroundingBlocks.get(side);
	}
	
	public TileEntityTank getTankInDirection(ForgeDirection direction) {
		if (neighbours.containsKey(direction)) {
			WeakReference<TileEntityTank> neighbour = neighbours.get(direction);
			if (neighbour != null) {
				TileEntityTank otherTank = neighbour.get();
				if (otherTank == null) {
					return null;
				}
				if (otherTank.isInvalid()) {
					return null;
				}
				if (this instanceof TileEntityTank) {
					if (otherTank.canReceiveLiquid(((TileEntityTank)this).getInternalTank().getLiquid())) {
						return otherTank;
					}
				}else {
					return otherTank;
				}
			}
		}
		return null;
	}
	
	public TileEntityTank[] getSurroundingTanks() {
		ArrayList<TileEntityTank> tanks = new ArrayList<TileEntityTank>();
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			TileEntityTank t = getTankInDirection(direction);
			if (t != null) {
				tanks.add(t);
			}
		}
		return tanks.toArray(new TileEntityTank[tanks.size()]);
	}
	
	public ArrayList<TileEntityTank> getHorizontalTanksOrdererdBySpace(HashSet<TileEntityTank> except) {
		ArrayList<TileEntityTank> horizontalTanks = new ArrayList<TileEntityTank>();
		for (ForgeDirection direction : horizontalDirections) {
			TileEntityTank tank = getTankInDirection(direction);
			if (tank != null && !except.contains(tank)) {
				horizontalTanks.add(tank);
			}
		}
		Collections.sort(horizontalTanks, sortBySpace);
		return horizontalTanks;
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
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Refresh the neighbours because something changed
	 */
	@Override
	public void onNeighbourChanged(int blockId) {
		findNeighbours();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {

	}

	@Override
	public void writeIdentifier(DataOutputStream dos) throws IOException {
		dos.writeInt(xCoord);
		dos.writeInt(yCoord);
		dos.writeInt(zCoord);
	}
	
	@Override
	public SyncMap getSyncMap() {
		return syncMap;
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		if (worldObj.isRemote) { 
			findNeighbours();
		}
		return true;
	}
}
