package openblocks.common.tileentity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
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

	private WeakReference<TileEntityMultiblock> multiblockOwner;
	protected Set<TileEntityMultiblock> multiblockChildren = Collections.newSetFromMap(new WeakHashMap<TileEntityMultiblock, Boolean>());
	protected boolean isMultiblockOwner = false;

	@Override
	public void initialize() {
		if (!worldObj.isRemote) { 
			if (getOwner() == null) {
				takeOwnership();
			}
		}
	}

	private void takeOwnership() {
		isMultiblockOwner = true;
		multiblockChildren.clear();
		ArrayList<TileEntityMultiblock> children = floodFill();
		clearChildren();
		for (TileEntityMultiblock child : children) {
			if (child != this) {
				child.clearChildren();
			}
			child.multiblockOwner = new WeakReference<TileEntityMultiblock>(this);
			multiblockChildren.add(child);
			if (child != this) {
				child.transferDataTo(this);
			}
			onChildAdded(child);
		}
	}

	public TileEntityMultiblock getOwner() {
		if (multiblockOwner != null) {
			TileEntityMultiblock ownertile = multiblockOwner.get();
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
			if (o != null) {
				o.clearChildren();
			}
			invalidate();
			isMultiblockOwner = false;
			Collection<ArrayList<TileEntityMultiblock>> branches = findBranches();
			ArrayList<TileEntityMultiblock> owners = new ArrayList<TileEntityMultiblock>();
			for(ArrayList<TileEntityMultiblock> branch : branches) {
				TileEntityMultiblock branchOwner = null;
				for (TileEntityMultiblock child : branch) {
					if (branchOwner == null) {
						branchOwner = child;
						owners.add(branchOwner);
					}
					child.clearChildren();
					if (child != branchOwner) {
						child.transferDataTo(branchOwner);
					}
					branchOwner.multiblockChildren.add(child);
					branchOwner.onChildAdded(child);
					child.multiblockOwner = new WeakReference<TileEntityMultiblock>(branchOwner);
				}
			}
			if (owners.size() > 0) {
				transferDataTo(owners.toArray(new TileEntityMultiblock[owners.size()]));
			}
		}
	}

	protected void clearChildren() {
		Iterator<TileEntityMultiblock> it = multiblockChildren.iterator();
		while (it.hasNext()) {
			TileEntityMultiblock next = it.next();
			it.remove();
			onChildRemoved(next);
		}
		multiblockChildren.clear();
	}

	public Set<TileEntityMultiblock> getChildren() {
		return multiblockChildren;
	}

	protected abstract void onChildAdded(TileEntityMultiblock tile);

	protected abstract void onChildRemoved(TileEntityMultiblock tile);

	@Override
	public void onChunkUnload() {
		if (!worldObj.isRemote) { 
			if (multiblockOwner != null) {
				TileEntityMultiblock o = multiblockOwner.get();
				if (o != null) {
					o.multiblockChildren.remove(this);
					o.onChildRemoved(this);
					for (TileEntityMultiblock child : o.getChildren()) {
						if (worldObj.blockExists(child.xCoord, child.yCoord, child.zCoord)) {
							child.takeOwnership();
						}
					}
				}
			}
		}
		super.onChunkUnload();
	}

	public abstract void transferDataTo(TileEntityMultiblock ... tile);

	@Override
	public abstract void onBlockAdded();

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

	public boolean isValidNeighbour(TileEntity tile) {
		return tile instanceof TileEntityMultiblock && !tile.isInvalid();
	}

	@Override
	public abstract boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ);

	@Override
	public abstract void onNeighbourChanged(int blockId);

	@Override
	public abstract void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, float hitX, float hitY, float hitZ);

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}
}
