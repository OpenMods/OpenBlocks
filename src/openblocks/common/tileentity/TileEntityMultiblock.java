package openblocks.common.tileentity;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;
import openblocks.utils.Coord;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityMultiblock extends TileEntity {

	public Collection<HashSet<TileEntity>> findBranches() {

		HashMap<ForgeDirection, HashSet<TileEntity>> branches = new HashMap<ForgeDirection, HashSet<TileEntity>>();

		HashSet<TileEntity> alreadyFound = new HashSet<TileEntity>();

		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {

			HashSet<TileEntity> branchTiles = floodFill(direction, alreadyFound);

			if (branchTiles.size() > 0) {
				branches.put(direction, branchTiles);
				alreadyFound.addAll(branchTiles);
			}

		}

		return branches.values();
	}

	public HashSet<TileEntity> floodFill() {
		return floodFill(null, null);
	}

	public HashSet<TileEntity> floodFill(ForgeDirection direction, HashSet<TileEntity> unlessMatches) {

		HashSet<Coord> validCoords = new HashSet<Coord>();
		HashSet<Coord> checkedCoords = new HashSet<Coord>();
		Queue<Coord> coordQueue = new LinkedList<Coord>();
		HashSet<TileEntity> validTiles = new HashSet<TileEntity>();

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

			TileEntity checkTile = worldObj.getBlockTileEntity(blockX, blockY, blockZ);

			if (checkTile instanceof TileEntityMultiblock
					&& isValidNeighbour(checkTile)) {

				if (unlessMatches != null && unlessMatches.contains(checkTile)) { return validTiles; }

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

			checkedCoords.add(coord);
		}

		if (coordQueue.size() > 0) {
			validTiles.clear();
		}

		return validTiles;
	}

	public boolean isValidNeighbour(TileEntity tile) {
		return tile instanceof TileEntityHealBlock && !tile.isInvalid();
	}

	public ISyncableObject replaceObject(ISyncableObject current, ISyncableObject newObj) {
		if (newObj == null) {
			if (current != null) {
				current.unregisterTile(this);
				current = null;
			}
		} else {
			if (current != null) {
				current.unregisterTile(this);
				newObj.merge(current);
				current.clear();
			}
			current = newObj;
			current.registerTile(this);
		}
		return current;
	}
}
