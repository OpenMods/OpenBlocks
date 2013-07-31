package openblocks.common.tileentity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import openblocks.OpenBlocks;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapTile;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableInt;
import openblocks.sync.SyncableIntArray;
import openblocks.utils.Coord;

public class TileEntityValve extends TileEntity implements ITankContainer,
		ISyncHandler {

	public enum Keys {
		tankAmount,
		tankCapacity,
		flags,
		linkedTiles,
		liquidId,
		liquidMeta
	}

	public enum Flags {
		enabled,
		isOwner
	}

	public static final int CAPACITY_PER_TANK = LiquidContainerRegistry.BUCKET_VOLUME * 8;

	private ForgeDirection direction = ForgeDirection.EAST;

	private int checkTicker = 0;

	private boolean needsRecheck = false;

	private HashMap<Integer, Double> spread = new HashMap<Integer, Double>();
	private HashMap<Integer, Integer> levelCapacity = new HashMap<Integer, Integer>();

	public final LiquidTank tank = new LiquidTank(CAPACITY_PER_TANK);

	private SyncMapTile syncMap = new SyncMapTile();

	// synced with the client
	private SyncableInt tankAmount = new SyncableInt(0);
	private SyncableInt tankCapacity = new SyncableInt(0);
	private SyncableInt tankLiquidId = new SyncableInt(0);
	private SyncableInt tankLiquidMeta = new SyncableInt(0);
	private SyncableFlags flags = new SyncableFlags();
	private SyncableIntArray linkedTiles = new SyncableIntArray();
	
	// not streamed downstream
	private SyncableInt ownerX = new SyncableInt(0);
	private SyncableInt ownerY = new SyncableInt(0);
	private SyncableInt ownerZ = new SyncableInt(0);

	public TileEntityValve() {
		syncMap.put(Keys.tankAmount, tankAmount);
		syncMap.put(Keys.tankCapacity, tankCapacity);
		syncMap.put(Keys.flags, flags);
		syncMap.put(Keys.linkedTiles, linkedTiles);
		syncMap.put(Keys.liquidId, tankLiquidId);
		syncMap.put(Keys.liquidMeta, tankLiquidMeta);
	}

	public int[] getLinkedCoords() {
		return (int[]) linkedTiles.getValue();
	}

	public void destroyTank() {
		if (!linkedTiles.isEmpty()) {
			int[] coords = (int[]) linkedTiles.getValue();
			for (int i = 0; i < coords.length; i += 3) {
				int x = xCoord + coords[i];
				int y = yCoord + coords[i + 1];
				int z = zCoord + coords[i + 2];
				if (worldObj.getBlockId(x, y, z) == OpenBlocks.Config.blockTankId) {
					worldObj.setBlock(x, y, z, 0, 0, 2);
				}
			}
		}
		flags.off(Flags.enabled);
		tankCapacity.setValue(0);
		tankAmount.setValue(0);
		linkedTiles.clear();
		TileEntityValve parent = getParentTile();
		if (parent != null) {
			parent.markForRecheck();
		}
	}
	
	public void onBlockPlaced() {
		flags.set(Flags.isOwner, true);
		needsRecheck = true;
	}

	@Override
	public void updateEntity() {
		
		if (!worldObj.isRemote) {
			LiquidStack liquid = tank.getLiquid();
			tankAmount.setValue(liquid == null ? 0 : liquid.amount);
			if (liquid != null) {
				tankLiquidId.setValue(liquid.itemID);
				tankLiquidMeta.setValue(liquid.itemMeta);
			}
			if (needsRecheck) {
				checkTank();
			}
		}
		syncMap.sync(worldObj, this, (double) xCoord, (double) yCoord,
				(double) zCoord);

	}

	public void markForRecheck() {
		needsRecheck = true;
	}

	public HashMap<Integer, Double> getSpread() {
		return spread;
	}
	
	public void setOwner(TileEntityValve parent) {
		ownerX.setValue(parent.xCoord);
		ownerY.setValue(parent.yCoord);
		ownerZ.setValue(parent.zCoord);
		int filledBy = parent.fill(ForgeDirection.UNKNOWN, getTank().getLiquid(), true);
		tank.drain(filledBy, true);
		flags.set(Flags.isOwner, false);
		flags.set(Flags.enabled, false);
	}

	public void checkTank() {
		if (!worldObj.isRemote) {

			flags.on(Flags.isOwner);
			
			needsRecheck = false;

			HashSet<Coord> validAreas = new HashSet<Coord>();
			HashSet<Coord> checkedAreas = new HashSet<Coord>();
			HashSet<Coord> otherValves = new HashSet<Coord>();

			Queue<Coord> queue = new LinkedList<Coord>();

			// add the coordinate in the direction the valve is facing. that's
			// out starting block
			Coord pos = new Coord(direction.offsetX, direction.offsetY, direction.offsetZ);
			queue.add(pos);

			Coord coord;
			Coord checkCoord = new Coord();

			while (queue.size() > 0 && queue.size() < 2000) {

				coord = queue.poll();

				int blockX = xCoord + coord.x;
				int blockY = yCoord + coord.y;
				int blockZ = zCoord + coord.z;

				// if the block exists (chunk is loaded)
				if (worldObj.blockExists(blockX, blockY, blockZ)) {

					int blockId = worldObj.getBlockId(blockX, blockY, blockZ);

					// ok, it's air or it's a 'tank' (fake) block, so it's valid
					if (worldObj.isAirBlock(blockX, blockY, blockZ)
							|| blockId == OpenBlocks.Config.blockTankId) {

						validAreas.add(coord);

						// if it's within bounds
						if (coord.x > -127 && coord.x < 127 && coord.y > -127
								&& coord.y < 127 && coord.z > -127
								&& coord.z < 127) {

							checkCoord.setFrom(coord);
							checkCoord.offset(1, 0, 0); // 1, 0, 0
							if (!checkedAreas.contains(checkCoord)
									&& !queue.contains(checkCoord)) {
								queue.add(checkCoord.clone());
							}

							checkCoord.offset(-1, 1, 0); // 0, 1, 0
							if (!checkedAreas.contains(checkCoord)
									&& !queue.contains(checkCoord)) {
								queue.add(checkCoord.clone());
							}

							checkCoord.offset(0, -1, 1); // 0, 0, 1
							if (!checkedAreas.contains(checkCoord)
									&& !queue.contains(checkCoord)) {
								queue.add(checkCoord.clone());
							}

							checkCoord.offset(-1, 0, -1); // -1, 0, 0
							if (!checkedAreas.contains(checkCoord)
									&& !queue.contains(checkCoord)) {
								queue.add(checkCoord.clone());
							}

							checkCoord.offset(1, -1, 0); // 0, -1, 0
							if (!checkedAreas.contains(checkCoord)
									&& !queue.contains(checkCoord)) {
								queue.add(checkCoord.clone());
							}

							checkCoord.offset(0, 1, -1); // 0, 0, -1
							if (!checkedAreas.contains(checkCoord)
									&& !queue.contains(checkCoord)) {
								queue.add(checkCoord.clone());
							}
						}
					}else if (blockId == OpenBlocks.Config.blockValveId) {
						if (coord.x != 0 || coord.y != 0 || coord.z != 0) {
							otherValves.add(coord);
						}
					}
				}
				checkedAreas.add(coord);
			}

			// none left in the queue, this means we've got a complete area
			if (queue.size() == 0) {
				
				flags.on(Flags.enabled);
				
				// make all the others children to this one
				// and copy their liquid across
				TileEntity te = null;
				for (Coord other : otherValves) {
					te = worldObj.getBlockTileEntity(xCoord + other.x, yCoord + other.y, zCoord + other.z);
					if (te != null && te instanceof TileEntityValve) {
						((TileEntityValve) te).setOwner(this);
					}
				}
				
				for (Coord validCoord : validAreas) {
					int x = xCoord + validCoord.x;
					int y = yCoord + validCoord.y;
					int z = zCoord + validCoord.z;
					worldObj.setBlock(x, y, z, OpenBlocks.Config.blockTankId, 0, 2);
					te = worldObj.getBlockTileEntity(x, y, z);
					if (te != null && te instanceof TileEntityTank) {
						TileEntityTank tankBlock = (TileEntityTank) te;
						tankBlock.setValve(this);
					}
				}
			} else {
				destroyTank();
				return;
			}

			if (!linkedTiles.isEmpty()) {
				int[] alreadyLinked = (int[]) linkedTiles.getValue();
				Coord alreadyLinkedCoord = new Coord();
				for (int i = 0; i < alreadyLinked.length; i += 3) {
					int x = alreadyLinked[i];
					int y = alreadyLinked[i + 1];
					int z = alreadyLinked[i + 2];
					alreadyLinkedCoord.set(x, y, z);
					if (!validAreas.contains(alreadyLinkedCoord)) {
						x += xCoord;
						y += yCoord;
						z += zCoord;
						if (worldObj.getBlockId(x, y, z) == OpenBlocks.Config.blockTankId) {
							worldObj.setBlock(x, y, z, 0, 0, 2);
						}
					}
				}
			}

			int[] newLinkedTiles = coordsToIntArray(validAreas);
			linkedTiles.setValue(newLinkedTiles);
			int capacity = newLinkedTiles.length * (CAPACITY_PER_TANK);
			tankCapacity.setValue(capacity);
			tank.setCapacity(capacity);
		}
	}
	
	private int[] coordsToIntArray(Set<Coord> coords) {
		int[] arr = new int[coords.size() * 3];
		int i = 0;
		for (Coord coord : coords) {
			arr[i++] = coord.x;
			arr[i++] = coord.y;
			arr[i++] = coord.z;
		}
		return arr;
	}

	public boolean isOwner() {
		return flags.get(Flags.isOwner);
	}
	
	public void setDirection(ForgeDirection direction) {
		this.direction = direction;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
		tankCapacity.writeToNBT(tag, "tankCapacity");
		linkedTiles.writeToNBT(tag, "linkedTiles");
		flags.writeToNBT(tag, "flags");
		ownerX.writeToNBT(tag, "ownerX");
		ownerY.writeToNBT(tag, "ownerY");
		ownerZ.writeToNBT(tag, "ownerZ");
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
		tankCapacity.readFromNBT(tag, "tankCapacity");
		tank.setCapacity((Integer) tankCapacity.getValue());
		flags.readFromNBT(tag, "flags");
		linkedTiles.readFromNBT(tag, "linkedTiles");
		ownerX.readFromNBT(tag, "ownerX");
		ownerY.readFromNBT(tag, "ownerY");
		ownerZ.readFromNBT(tag, "ownerZ");
		if (tag.hasKey("direction")) {
			direction = ForgeDirection.getOrientation(tag
					.getInteger("direction"));
		}
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		ILiquidTank parentTank = getParentTank();
		if (parentTank != null) {
			return parentTank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		ILiquidTank parentTank = getParentTank();
		if (parentTank != null) {
			return parentTank.drain(maxDrain, doDrain);
		}
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[] { getParentTank() };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return getParentTank();
	}
	
	public ILiquidTank getTank() {
		return tank;
	}

	public LiquidStack getLiquid() {
		return getTank().getLiquid();
	}

	public boolean isEnabled() {
		return flags.get(Flags.enabled);
	}
	
	private TileEntityValve getParentTile() {
		if (!flags.get(Flags.isOwner)) {
			TileEntity te = worldObj.getBlockTileEntity(
					(Integer)ownerX.getValue(),
					(Integer)ownerY.getValue(),
					(Integer)ownerZ.getValue());
			if (te != null && te instanceof TileEntityValve) {
				return (TileEntityValve) te;
			}
		}
		return this;
	}
	
	public ILiquidTank getParentTank() {
		TileEntityValve valve = getParentTile();
		if (valve != null) {
			return valve.getTank();
		}
		return null;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		if (worldObj.isRemote) {
			if (!flags.get(Flags.enabled)) {
				spread.clear();
				return;
			}
			LiquidStack liquid = tank.getLiquid();
			boolean recreateLiquid = false;
			if (liquid == null || !tankLiquidId.equals(liquid.itemID)
					|| !tankLiquidMeta.equals(liquid.itemMeta)) {
				recreateLiquid = true;
			}
			tank.setCapacity((Integer) tankCapacity.getValue());
			if (recreateLiquid) {
				LiquidStack newLiquid = new LiquidStack(
						(Integer) tankLiquidId.getValue(),
						(Integer) tankCapacity.getValue(),
						(Integer) tankLiquidMeta.getValue());
				tank.setLiquid(newLiquid);
			}
			int[] tiles = (int[]) linkedTiles.getValue();
			HashMap<Integer, Integer> levelCapacity = new HashMap<Integer, Integer>();
			for (int i = 0; i < tiles.length; i += 3) {
				int f = 0;
				int y = tiles[i + 1];
				if (levelCapacity.containsKey(y)) {
					f = levelCapacity.get(y);
				}
				f++;
				levelCapacity.put(y, f);
			}

			List<Integer> sortedKeys = new ArrayList<Integer>(
					levelCapacity.keySet());
			Collections.sort(sortedKeys);
			spread.clear();
			int remaining = (Integer) tankAmount.getValue();

			for (Integer level : sortedKeys) {
				int tanksOnLevel = levelCapacity.get(level);
				int capacityForLevel = CAPACITY_PER_TANK * tanksOnLevel;
				int usedByLevel = 0;
				if (remaining > 0) {
					usedByLevel = Math.min(remaining, capacityForLevel);
				}
				remaining -= usedByLevel;
				spread.put(level,
						((double) usedByLevel / (double) capacityForLevel));
			}
		}
	}

	@Override
	public SyncMap getSyncMap() {
		return syncMap;
	}

	@Override
	public void writeIdentifier(DataOutputStream dos) throws IOException {
		dos.writeInt(xCoord);
		dos.writeInt(yCoord);
		dos.writeInt(zCoord);
	}
}
