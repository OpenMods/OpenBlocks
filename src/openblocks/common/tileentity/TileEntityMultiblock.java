package openblocks.common.tileentity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.WeakHashMap;

import openblocks.api.IAwareTile;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;
import openblocks.utils.Coord;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public abstract class TileEntityMultiblock extends OpenTileEntity implements IAwareTile {
	
	private WeakReference<TileEntityMultiblock> owner;
	protected WeakHashMap<TileEntityMultiblock, Void> children = new WeakHashMap<TileEntityMultiblock, Void>();
	protected boolean isOwner = false;

	@Override
	public void initialize() {
		if (!worldObj.isRemote) { 
			if (getOwner() == null) {
				isOwner = true;
				children.clear();
				ArrayList<TileEntityMultiblock> children = floodFill();
				for (TileEntityMultiblock tile : children) {
					tile.setOwner(this);
				}
			}
		}
	}

	public TileEntityMultiblock getOwner() {
		if (owner != null) {
			TileEntityMultiblock ownertile = owner.get();
			if (ownertile != null && !ownertile.isInvalid() && ownertile.isActive()) {
				return ownertile;
			}
		}
		return null;
	}

	@Override
	public void onBlockBroken() {
		if (!worldObj.isRemote) { 
			TileEntityMultiblock o = getOwner();
			invalidate();
			children.clear();
			o.removeChild(this);
			if (o == this) {
				isOwner = false;
				Collection<ArrayList<TileEntityMultiblock>> branches = findBranches();
				ArrayList<TileEntityMultiblock> owners = new ArrayList<TileEntityMultiblock>();
				for(ArrayList<TileEntityMultiblock> branch : branches) {
					TileEntityMultiblock branchOwner = null;
					for (TileEntityMultiblock child : branch) {
						if (branchOwner == null) {
							branchOwner = child;
							owners.add(branchOwner);
						}
						child.setOwner(branchOwner);
					}
				}
				if (owners.size() > 0) {
					transferDataTo(owners.toArray(new TileEntityMultiblock[owners.size()]));
				}
			}
		}
	}
	
	protected void addChild(TileEntityMultiblock tile) {
		children.put(tile, null);
	}
	
	protected void removeChild(TileEntityMultiblock tile) {
		children.remove(tile);
	}
	
	protected void setOwner(TileEntityMultiblock tile) {
		tile.isOwner = true;
		tile.addChild(this);
		owner = new WeakReference(tile);
		if (tile != this) {
			isOwner = false;
			children.clear();
			transferDataTo(tile);
		}
	}


	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		getOwner().removeChild(this);
	}
	
	public abstract void transferDataTo(TileEntityMultiblock ... tile);

	@Override
	public void onBlockAdded() {
	}
	
	
	public Collection<ArrayList<TileEntityMultiblock>> findBranches() {

		HashMap<ForgeDirection, ArrayList<TileEntityMultiblock>> branches = new HashMap<ForgeDirection, ArrayList<TileEntityMultiblock>>();

		HashSet<TileEntityMultiblock> alreadyFound = new HashSet<TileEntityMultiblock>();

		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {

			ArrayList<TileEntityMultiblock> branchTiles = floodFill(direction, alreadyFound);

			if (branchTiles.size() > 0) {
				branches.put(direction, branchTiles);
				alreadyFound.addAll(branchTiles);
			}

		}

		return branches.values();
	}

	public ArrayList<TileEntityMultiblock> floodFill() {
		return floodFill(null, null);
	}

	public ArrayList<TileEntityMultiblock> floodFill(ForgeDirection direction, HashSet<TileEntityMultiblock> unlessMatches) {

		HashSet<Coord> validCoords = new HashSet<Coord>();
		HashSet<Coord> checkedCoords = new HashSet<Coord>();
		Queue<Coord> coordQueue = new LinkedList<Coord>();
		ArrayList<TileEntityMultiblock> validTiles = new ArrayList<TileEntityMultiblock>();

		// add our starting position
		Coord pos = new Coord();
		if (direction != null) {
			pos.offset(direction);
		}
		coordQueue.add(pos);

		Coord checkCoord = new Coord();

		while (coordQueue.size() > 0 && checkedCoords.size() < 400) {

			Coord coord = coordQueue.poll();

			int blockX = xCoord + coord.x;
			int blockY = yCoord + coord.y;
			int blockZ = zCoord + coord.z;
			
			if (worldObj.blockExists(blockX, blockY, blockZ)) {

				TileEntity tile = worldObj.getBlockTileEntity(blockX, blockY, blockZ);
	
				if (tile instanceof TileEntityMultiblock) {
					
					TileEntityMultiblock checkTile = (TileEntityMultiblock) tile;
					
					if (isValidNeighbour(checkTile)) {
						
						if (unlessMatches != null && unlessMatches.contains(checkTile)) {
							return validTiles;
						}
		
						validCoords.add(coord);
						validTiles.add(checkTile);
		
						for (ForgeDirection checkDirection : ForgeDirection.VALID_DIRECTIONS) {
		
							checkCoord.setFrom(coord);
							checkCoord.offset(checkDirection);
		
							if (!checkedCoords.contains(checkCoord)
									&& !coordQueue.contains(checkCoord)) {
								coordQueue.add(checkCoord.clone());
							}
						}
					}
				}
	
				checkedCoords.add(coord);
			}
		}

		if (coordQueue.size() > 0) {
			validTiles.clear();
		}

		return validTiles;
	}

	public boolean isValidNeighbour(TileEntityMultiblock tile) {
		return !tile.isInvalid();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}
}
