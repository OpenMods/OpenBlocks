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
import openblocks.network.ISyncableObject;
import openblocks.network.ISyncHandler;
import openblocks.network.SyncMap;
import openblocks.network.SyncMapTile;
import openblocks.network.SyncableFlags;
import openblocks.network.SyncableInt;
import openblocks.network.SyncableIntArray;
import openblocks.utils.Coord;

public class TileEntityValve extends TileEntity implements ITankContainer,
		ISyncHandler {

	public static enum Keys {
		tankAmount, tankCapacity, flags, linkedTiles, liquidId, liquidMeta
	}

	public enum Flags {
		enabled
	}

	public static final int CAPACITY_PER_TANK = LiquidContainerRegistry.BUCKET_VOLUME * 16;

	private ForgeDirection direction = ForgeDirection.EAST;

	private int checkTicker = 0;

	private boolean needsRecheck = false;

	private HashMap<Integer, Double> spread = new HashMap<Integer, Double>();
	private HashMap<Integer, Integer> levelCapacity = new HashMap<Integer, Integer>();

	public final LiquidTank tank = new LiquidTank(CAPACITY_PER_TANK);

	private SyncMapTile syncMap = new SyncMapTile();

	private SyncableInt tankAmount = new SyncableInt(0);
	private SyncableInt tankCapacity = new SyncableInt(0);
	private SyncableInt tankLiquidId = new SyncableInt(0);
	private SyncableInt tankLiquidMeta = new SyncableInt(0);
	private SyncableFlags flags = new SyncableFlags();
	private SyncableIntArray linkedTiles = new SyncableIntArray();

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

	public void checkTank() {
		if (!worldObj.isRemote) {

			needsRecheck = false;

			HashSet<Coord> validAreas = new HashSet<Coord>();
			HashSet<Coord> checkedAreas = new HashSet<Coord>();

			Queue<Coord> queue = new LinkedList<Coord>();

			// add the coordinate in the direction the valve is facing. that's
			// out starting block
			Coord pos = new Coord(direction.offsetX, direction.offsetY,
					direction.offsetZ);
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
							if (!checkedAreas.contains(checkCoord)) {
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
					}
				}
				checkedAreas.add(coord);
			}
			if (queue.size() == 0) {
				flags.on(Flags.enabled);
				for (Coord validCoord : validAreas) {
					int x = xCoord + validCoord.x;
					int y = yCoord + validCoord.y;
					int z = zCoord + validCoord.z;
					worldObj.setBlock(x, y, z, OpenBlocks.Config.blockTankId,
							0, 2);
					TileEntity te = worldObj.getBlockTileEntity(x, y, z);
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

			int[] newLinkedTiles = new int[validAreas.size() * 3];
			int i = 0;
			for (Coord validCoord : validAreas) {
				newLinkedTiles[i++] = validCoord.x;
				newLinkedTiles[i++] = validCoord.y;
				newLinkedTiles[i++] = validCoord.z;
			}
			linkedTiles.setValue(newLinkedTiles);
			int capacity = newLinkedTiles.length * (CAPACITY_PER_TANK);
			tankCapacity.setValue(capacity);
			tank.setCapacity(capacity);
		}
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
		if (tag.hasKey("direction")) {
			direction = ForgeDirection.getOrientation(tag
					.getInteger("direction"));
		}
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		if (!flags.get(Flags.enabled))
			return 0;
		return fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		if (!flags.get(Flags.enabled))
			return 0;
		int filled = tank.fill(resource, doFill);
		return filled;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (!flags.get(Flags.enabled))
			return null;
		return drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		if (!flags.get(Flags.enabled))
			return null;
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[] { tank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return tank;
	}

	public LiquidStack getLiquid() {
		return tank.getLiquid();
	}

	public boolean isEnabled() {
		return flags.get(Flags.enabled);
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		if (worldObj.isRemote) {
			if (!flags.get(Flags.enabled)) {
				spread.clear();
				System.out.println(linkedTiles.size());
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
