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

public class TileEntityValve extends TileEntity implements ITankContainer {

	private ForgeDirection direction = ForgeDirection.EAST;
	private boolean needsRecheck = false;
	private boolean enabled = false;

	private int checkTicker = 0;

	private HashMap<Integer, Double> spread = new HashMap<Integer, Double>();
	private HashMap<Integer, Integer> levelCapacity = new HashMap<Integer, Integer>();

	private HashMap<Coord, Void> linkedCoords = null;

	public static int capacityPerTank = LiquidContainerRegistry.BUCKET_VOLUME * 16;

	public final LiquidTank tank = new LiquidTank(capacityPerTank);

	public Set<Coord> getLinkedCoords() {
		return linkedCoords != null ? linkedCoords.keySet() : null;
	}

	public void destroyTank() {
		if (linkedCoords != null) {
			for (Coord linkedCoord : linkedCoords.keySet()) {
				int x = xCoord + linkedCoord.x;
				int y = yCoord + linkedCoord.y;
				int z = zCoord + linkedCoord.z;
				if (worldObj.getBlockId(x, y, z) == OpenBlocks.Config.blockTankId) {
					worldObj.setBlockToAir(x, y, z);
				}
			}
		}
		linkedCoords = null;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void updateEntity() {
		if (worldObj != null && !worldObj.isRemote) {
			if (needsRecheck && ++checkTicker % 15 == 0) {
				checkTank();
				needsRecheck = false;
			}

			// TODO: optimise. No point sending everything every fecking 20
			// ticks
			if (checkTicker % 20 == 0) {
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
			HashMap<Coord, Void> validAreas = new HashMap<Coord, Void>();
			HashMap<Coord, Void> checkedAreas = new HashMap<Coord, Void>();

			Queue<Coord> queue = new LinkedBlockingQueue<Coord>();
			queue.add(new Coord(direction.offsetX, direction.offsetY,
					direction.offsetZ));

			while (queue.size() > 0 && validAreas.size() < 100) {
				Coord coord = queue.poll();
				int blockId = worldObj.getBlockId(xCoord + coord.x, yCoord
						+ coord.y, zCoord + coord.z);
				if (blockId == 0 || blockId == OpenBlocks.Config.blockTankId) {
					validAreas.put(coord, null);
					if (coord.x > -127 && coord.x < 127 && coord.y > -127
							&& coord.y < 127 && coord.z > -127 && coord.z < 127) {
						Coord c = new Coord(coord.x + 1, coord.y, coord.z);
						if (!checkedAreas.containsKey(c)) {
							queue.add(c);
						}
						c = new Coord(coord.x, coord.y + 1, coord.z);
						if (!checkedAreas.containsKey(c)) {
							queue.add(c);
						}
						c = new Coord(coord.x, coord.y, coord.z + 1);
						if (!checkedAreas.containsKey(c)) {
							queue.add(c);
						}
						c = new Coord(coord.x - 1, coord.y, coord.z);
						if (!checkedAreas.containsKey(c)) {
							queue.add(c);
						}
						c = new Coord(coord.x, coord.y - 1, coord.z);
						if (!checkedAreas.containsKey(c)) {
							queue.add(c);
						}
						c = new Coord(coord.x, coord.y, coord.z - 1);
						if (!checkedAreas.containsKey(c)) {
							queue.add(c);
						}
					}
				}
				checkedAreas.put(coord, null);
			}

			if (queue.size() == 0) {
				enabled = true;
				for (Coord coord : validAreas.keySet()) {
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
				enabled = false;
				destroyTank();
				return;
			}

			if (linkedCoords != null) {
				for (Coord linkedCoord : linkedCoords.keySet()) {
					if (!validAreas.containsKey(linkedCoord)) {
						int x = xCoord + linkedCoord.x;
						int y = yCoord + linkedCoord.y;
						int z = zCoord + linkedCoord.z;
						if (worldObj.getBlockId(x, y, z) == OpenBlocks.Config.blockTankId) {
							worldObj.setBlockToAir(x, y, z);
						}
					}
				}
			}

			linkedCoords = validAreas;
			if (linkedCoords != null) {
				tank.setCapacity(linkedCoords.size() * (capacityPerTank));
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		writeToNBT(nbt);
		packet.customParam1 = nbt;
		return packet;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.customParam1);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
		if (linkedCoords != null) {
			int[] coords = new int[linkedCoords.size() * 3];
			int j = 0;
			for (Coord coord : linkedCoords.keySet()) {
				coords[j++] = coord.x;
				coords[j++] = coord.y;
				coords[j++] = coord.z;
			}
			tag.setIntArray("coords", coords);
		}
		tag.setBoolean("enabled", enabled);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
		linkedCoords = new HashMap<Coord, Void>();
		levelCapacity = new HashMap<Integer, Integer>();
		spread = new HashMap<Integer, Double>();
		if (tag.hasKey("enabled")) {
			enabled = tag.getBoolean("enabled");
		}
		if (tag.hasKey("coords")) {
			int[] coords = tag.getIntArray("coords");
			int x = 0;
			int y = 0;
			int z = 0;
			for (int i = 0; i < coords.length; i++) {
				switch (i % 3) {
				case 0:
					x = coords[i];
					break;
				case 1:
					y = coords[i];
					break;
				default:
					z = coords[i];
					linkedCoords.put(new Coord(x, y, z), null);
					int levelSpread = 0;
					if (levelCapacity.containsKey(y)) {
						levelSpread = levelCapacity.get(y);
					}
					levelCapacity.put(y, levelSpread + 1);
				}
			}
		}
		recalculateSpread();
	}

	private void recalculateSpread() {
		List<Integer> sortedKeys = new ArrayList<Integer>(
				levelCapacity.keySet());
		Collections.sort(sortedKeys);
		LiquidStack liquid = tank.getLiquid();
		int remaining = liquid != null ? liquid.amount : 0;
		System.out.println("Remaining = " + remaining);
		for (Integer level : sortedKeys) {
			int tanksOnLevel = levelCapacity.get(level);
			int capacityForLevel = capacityPerTank * tanksOnLevel;
			int usedByLevel = 0;
			if (remaining > 0) {
				usedByLevel = Math.min(remaining, capacityForLevel);
			}
			System.out.println("Used = " + usedByLevel);
			remaining -= usedByLevel;
			spread.put(level,
					((double) usedByLevel / (double) capacityForLevel));
		}
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
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
}
