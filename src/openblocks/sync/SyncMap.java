package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.network.PacketHandler;
import openblocks.utils.ByteUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.Player;

public abstract class SyncMap<H extends ISyncHandler> {

	public enum HandlerType {
		TILE_ENTITY {

			@Override
			public ISyncHandler findHandler(World world, DataInput input) throws IOException {
				int x = input.readInt();
				int y = input.readInt();
				int z = input.readInt();
				if (world != null) {
					if (world.blockExists(x, y, z)) {
						TileEntity tile = world.getBlockTileEntity(x, y, z);
						if (tile instanceof ISyncHandler)
						return (ISyncHandler)tile;
					}
				}

				Log.warn("Invalid handler info: can't find ISyncHandler TE @ (%d,%d,%d)", x, y, z);
				return null;
			}

			@Override
			public void writeHandlerInfo(ISyncHandler handler, DataOutput output) throws IOException {
				try {
					TileEntity te = (TileEntity)handler;
					output.writeInt(te.xCoord);
					output.writeInt(te.yCoord);
					output.writeInt(te.zCoord);
				} catch (ClassCastException e) {
					throw new RuntimeException("Invalid usage of handler type", e);
				}
			}

		},
		ENTITY {

			@Override
			public ISyncHandler findHandler(World world, DataInput input) throws IOException {
				int entityId = input.readInt();
				Entity entity = world.getEntityByID(entityId);
				if (entity instanceof ISyncHandler)
				return (ISyncHandler)entity;

				Log.warn("Invalid handler info: can't find ISyncHandler entity id %d", entityId);
				return null;
			}

			@Override
			public void writeHandlerInfo(ISyncHandler handler, DataOutput output) throws IOException {
				try {
					Entity e = (Entity)handler;
					output.writeInt(e.entityId);
				} catch (ClassCastException e) {
					throw new RuntimeException("Invalid usage of handler type", e);
				}
			}

		};

		public abstract ISyncHandler findHandler(World world, DataInput input) throws IOException;

		public abstract void writeHandlerInfo(ISyncHandler handler, DataOutput output) throws IOException;

		private static final HandlerType[] TYPES = values();
	}

	protected final H handler;

	private Set<Integer> knownUsers = new HashSet<Integer>();

	protected ISyncableObject[] objects = new ISyncableObject[16];
	protected HashMap<String, Integer> nameMap = new HashMap<String, Integer>();

	private int index = 0;

	protected SyncMap(H handler) {
		this.handler = handler;
	}

	public void put(String name, ISyncableObject value) {
		nameMap.put(name, index);
		objects[index++] = value;
	}

	public ISyncableObject get(String name) {
		if (nameMap.containsKey(name)) { return objects[nameMap.get(name)]; }
		return null;
	}

	public int size() {
		return index;
	}

	public List<ISyncableObject> readFromStream(DataInput dis) throws IOException {
		short mask = dis.readShort();
		List<ISyncableObject> changes = new ArrayList<ISyncableObject>();
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null) {
				if (ByteUtils.get(mask, i)) {
					objects[i].readFromStream(dis);
					changes.add(objects[i]);
					objects[i].resetChangeTimer(getWorld());
				}
			}
		}
		return changes;
	}

	public void writeToStream(DataOutput dos, boolean regardless) throws IOException {
		short mask = 0;
		for (int i = 0; i < 16; i++) {
			mask = ByteUtils.set(mask, i, objects[i] != null
					&& (regardless || objects[i].isDirty()));
		}
		dos.writeShort(mask);
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null && (regardless || objects[i].isDirty())) {
				objects[i].writeToStream(dos, regardless);
				objects[i].resetChangeTimer(getWorld());
			}
		}
	}

	public void markAllAsClean() {
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null) {
				objects[i].markClean();
			}
		}
	}

	protected abstract HandlerType getHandlerType();

	protected abstract Set<EntityPlayer> getPlayersWatching();

	protected abstract World getWorld();

	public boolean sync() {
		Set<EntityPlayer> players = getPlayersWatching();
		boolean sent = false;
		if (!getWorld().isRemote) {
			Packet changePacket = null;
			Packet fullPacket = null;

			boolean hasChanges = hasChanges();
			try {
				Set<Integer> newUsersInRange = Sets.newHashSet();
				for (EntityPlayer player : players) {
					newUsersInRange.add(player.entityId);
					if (knownUsers.contains(player.entityId)) {
						if (hasChanges) {
							if (changePacket == null) changePacket = createPacket(false, false);
							OpenBlocks.proxy.sendPacketToPlayer((Player)player, changePacket);
							sent = true;
						}
					} else {
						if (fullPacket == null) fullPacket = createPacket(true, false);
						OpenBlocks.proxy.sendPacketToPlayer((Player)player, fullPacket);
						sent = true;
					}
				}
				knownUsers = newUsersInRange;
			} catch (IOException e) {
				Log.warn(e, "IOError during sync");
			}
		} else {
			try {
				OpenBlocks.proxy.sendPacketToServer(createPacket(false, true));
				sent = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			knownUsers.clear();
		}
		markAllAsClean();
		return sent;
	}

	private boolean hasChanges() {
		for (ISyncableObject obj : objects) {
			if (obj != null && obj.isDirty()) return true;
		}

		return false;
	}

	public Packet createPacket(boolean fullPacket, boolean toServer) throws IOException {
		ByteArrayDataOutput bos = ByteStreams.newDataOutput();
		bos.writeBoolean(toServer);
		if (toServer) {
			int dimension = getWorld().provider.dimensionId;
			bos.writeInt(dimension);
		}
		HandlerType type = getHandlerType();
		ByteUtils.writeVLI(bos, type.ordinal());
		type.writeHandlerInfo(handler, bos);
		writeToStream(bos, fullPacket);
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = PacketHandler.CHANNEL_SYNC;
		packet.data = bos.toByteArray();
		packet.length = packet.data.length;
		return packet;
	}

	public static ISyncHandler findSyncMap(World world, DataInput input) throws IOException {
		int handlerTypeId = ByteUtils.readVLI(input);

		// If this happens, abort! Serious bug!
		Preconditions.checkPositionIndex(handlerTypeId, HandlerType.TYPES.length, "handler type");

		HandlerType handlerType = HandlerType.TYPES[handlerTypeId];

		ISyncHandler handler = handlerType.findHandler(world, input);
		return handler;
	}
}
