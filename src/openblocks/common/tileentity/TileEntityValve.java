package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import openblocks.OpenBlocks;
import openblocks.network.IChangeListener;
import openblocks.network.ISyncableObject;
import openblocks.network.SyncMap;
import openblocks.network.SyncableFlags;
import openblocks.network.SyncableInt;
import openblocks.network.SyncableIntArray;
import openblocks.network.SyncableManager;
import openblocks.utils.Coord;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class TileEntityValve extends TileEntity implements ITankContainer, IChangeListener {

	public static enum Keys {
		tankAmount,
		tankCapacity,
		flags,
		linkedTiles
	}
	
	public static final int FLAG_ENABLED = 0;

	public static final int CAPACITY_PER_TANK = LiquidContainerRegistry.BUCKET_VOLUME * 16;
	
	private ForgeDirection direction = ForgeDirection.EAST;

	private int checkTicker = 0;
	
	private boolean needsRecheck = false;

	private HashMap<Integer, Double> spread = new HashMap<Integer, Double>();
	private HashMap<Integer, Integer> levelCapacity = new HashMap<Integer, Integer>();

	public final LiquidTank tank = new LiquidTank(CAPACITY_PER_TANK);
	
	private SyncMap syncMap = null;
	
	private SyncableInt tankAmount = new SyncableInt(0);
	private SyncableInt tankCapacity = new SyncableInt(0);
	private SyncableFlags flags = new SyncableFlags();
	private SyncableIntArray linkedTiles = new SyncableIntArray();
	
	public TileEntityValve() {
		linkedTiles.addChangeListener(this);
	}
	
	public int[] getLinkedCoords() {
		return (int[])linkedTiles.getValue();
	}

	public void destroyTank() {
		if (!linkedTiles.isEmpty()) {
			int[] coords = (int[])linkedTiles.getValue();
			for (int i = 0; i < coords.length; i += 3) {
				int x = xCoord + coords[i];
				int y = yCoord + coords[i+1];
				int z = zCoord + coords[i+2];
				if (worldObj.getBlockId(x, y, z) == OpenBlocks.Config.blockTankId) {
					worldObj.setBlockToAir(x, y, z);
				}
			}
		}
		linkedTiles.clear();
	}

	@Override
	public void updateEntity() {
		
		LiquidStack liquid = tank.getLiquid();
		tankAmount.setValue(liquid == null ? 0 : liquid.amount);
		
		initializeSync();
		
		if (worldObj != null && !worldObj.isRemote && checkTicker++ % 20 == 0) {
			if (needsRecheck) {
				checkTank();
			}
			syncMap.syncNearbyUsers(this);
		}

	}
	
	private void initializeSync() {
		if (syncMap == null) {
			syncMap = OpenBlocks.syncableManager.newSyncMap(!worldObj.isRemote);
			syncMap.put(Keys.tankAmount.ordinal(), tankAmount);
			syncMap.put(Keys.tankCapacity.ordinal(), tankCapacity);
			syncMap.put(Keys.flags.ordinal(), flags);
			syncMap.put(Keys.linkedTiles.ordinal(), linkedTiles);
			
			if (!worldObj.isRemote){
				System.out.println("Marking block for update");
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	public void markForRecheck() {
		needsRecheck = true;
	}

	public HashMap<Integer, Double> getSpread() {
		return spread;
	}

	public void checkTank() {

		if (!worldObj.isRemote) {
			HashMap<Integer, Coord> validAreas = new HashMap<Integer, Coord>();
			HashMap<Integer, Coord> checkedAreas = new HashMap<Integer, Coord>();

			Queue<Coord> queue = new LinkedBlockingQueue<Coord>();
			queue.add(new Coord(direction.offsetX, direction.offsetY,
					direction.offsetZ));

			while (queue.size() > 0 && validAreas.size() < 100) {
				Coord coord = queue.poll();
				int blockId = worldObj.getBlockId(xCoord + coord.x, yCoord + coord.y, zCoord + coord.z);
				if (blockId == 0 || blockId == OpenBlocks.Config.blockTankId) {
					validAreas.put(coord.getHash(), coord);
					if (coord.x > -127 && coord.x < 127 && coord.y > -127
							&& coord.y < 127 && coord.z > -127 && coord.z < 127) {
						int x = coord.x + 1;
						int y = coord.y;
						int z = coord.z;
						if (!checkedAreas.containsKey(Coord.getHash(x, y, z))) {
							queue.add(new Coord(x, y, z));
						}
						x--; y++;
						if (!checkedAreas.containsKey(Coord.getHash(x, y, z))) {
							queue.add(new Coord(x, y, z));
						}
						y--; z++;
						if (!checkedAreas.containsKey(Coord.getHash(x, y, z))) {
							queue.add(new Coord(x, y, z));
						}
						x--; z--;
						if (!checkedAreas.containsKey(Coord.getHash(x, y, z))) {
							queue.add(new Coord(x, y, z));
						}
						y--; x++;
						if (!checkedAreas.containsKey(Coord.getHash(x, y, z))) {
							queue.add(new Coord(x, y, z));
						}
						z--; y++;
						if (!checkedAreas.containsKey(Coord.getHash(x, y, z))) {
							queue.add(new Coord(x, y, z));
						}
					}
				}
				checkedAreas.put(coord.getHash(), coord);
			}
			

			if (queue.size() == 0) {
				flags.on(FLAG_ENABLED);
				for (Coord coord : validAreas.values()) {
					int x = xCoord + coord.x;
					int y = yCoord + coord.y;
					int z = zCoord + coord.z;
					worldObj.setBlock(x, y, z, OpenBlocks.Config.blockTankId);
					TileEntity te = worldObj.getBlockTileEntity(x, y, z);
					if (te != null && te instanceof TileEntityTank) {
						TileEntityTank tankBlock = (TileEntityTank) te;
						tankBlock.setValve(this);
					}
				}
			} else {
				flags.off(FLAG_ENABLED);
				destroyTank();
				return;
			}

			if (!linkedTiles.isEmpty()) {
				int[] alreadyLinked = (int[]) linkedTiles.getValue();
				for (int i = 0; i < alreadyLinked.length; i+= 3) {
					int x = alreadyLinked[i];
					int y = alreadyLinked[i+1];
					int z = alreadyLinked[i+2];
					int hash = Coord.getHash(x, y, z);
					if (!validAreas.containsKey(hash)) {
						x += xCoord;
						y += yCoord;
						z += zCoord;
						if (worldObj.getBlockId(x, y, z) == OpenBlocks.Config.blockTankId) {
							worldObj.setBlockToAir(x, y, z);
						}
					}
				}
			}

			int[] newLinkedTiles = new int[validAreas.size() * 3];
			int i = 0;
			for (Coord coord : validAreas.values()) {
				newLinkedTiles[i++] = coord.x;
				newLinkedTiles[i++] = coord.y;
				newLinkedTiles[i++] = coord.z;
			}
			linkedTiles.setValue(newLinkedTiles);
			tank.setCapacity(newLinkedTiles.length * (CAPACITY_PER_TANK));
		}
	}

	public void setDirection(ForgeDirection direction) {
		this.direction = direction;
	}

	@Override
	public Packet getDescriptionPacket() {
		Packet132TileEntityData packet = new Packet132TileEntityData();
		packet.actionType = 0;
		packet.xPosition = xCoord;
		packet.yPosition = yCoord;
		packet.zPosition = zCoord;
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNetwork(nbt);
		packet.customParam1 = nbt;
		System.out.println("Got packet");
		return packet;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNetwork(pkt.customParam1);
	}

	public void writeToNetwork(NBTTagCompound tag) {
		if (syncMap != null) {
			syncMap.writeToNetwork(tag);
		}
	}
	
	public void readFromNetwork(NBTTagCompound tag) {
		initializeSync();
		syncMap.readFromNetwork(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
		tankCapacity.writeToNBT(tag, "tankCapacity");
		linkedTiles.writeToNBT(tag, "linkedTiles");
		flags.writeToNBT(tag, "flags");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
		tankCapacity.readFromNBT(tag, "tankCapacity");
		tank.setCapacity((Integer)tankCapacity.getValue());
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		int filled = tank.fill(resource, doFill);
		return filled;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
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

	@Override
	public void onChanged(ISyncableObject object) {
		if (object == linkedTiles) {
			if (worldObj.isRemote){

				int[] tiles = (int[]) linkedTiles.getValue();
				HashMap<Integer, Integer> levelCapacity = new HashMap<Integer, Integer>();
				for (int i = 0; i < tiles.length; i+=3) {
					int f = 0;
					int y = tiles[i + 1];
					if (levelCapacity.containsKey(y)) {
						f = levelCapacity.get(y);
					}
					f++;
					levelCapacity.put(y, f);
				}
				
				List<Integer> sortedKeys = new ArrayList<Integer>(levelCapacity.keySet());
				Collections.sort(sortedKeys);
				spread.clear();
				int remaining = (Integer)tankAmount.getValue();

				for (Integer level : sortedKeys) {
					int tanksOnLevel = levelCapacity.get(level);
					int capacityForLevel = CAPACITY_PER_TANK * tanksOnLevel;
					int usedByLevel = 0;
					if (remaining > 0) {
						usedByLevel = Math.min(remaining, capacityForLevel);
					}
					remaining -= usedByLevel;
					spread.put(level, ((double) usedByLevel / (double) capacityForLevel));
				}
			}
		} else if (object == tankAmount) {
			System.out.println("synced tank amount");
		}
	}
}
