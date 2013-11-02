package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private ISyncableObject[] objects = new ISyncableObject[16];

	private int index = 0;

	protected SyncMap(H handler) {
		this.handler = handler;
	}

	public void put(ISyncableObject value) {
		objects[index++] = value;
	}

	public ISyncableObject get(Enum<?> id) {
		return get(id.ordinal());
	}

	public ISyncableObject get(int id) {
		return objects[id];
	}

	public List<ISyncableObject> readFromStream(World world, DataInput dis) throws IOException {
		short mask = dis.readShort();
		List<ISyncableObject> changes = new ArrayList<ISyncableObject>();
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null) {
				if (ByteUtils.get(mask, i)) {
					objects[i].readFromStream(dis);
					changes.add(objects[i]);
					objects[i].resetChangeTimer(world);
				}
			}
		}
		return changes;
	}

	public void writeToStream(World world, DataOutput dos, boolean regardless) throws IOException {
		short mask = 0;
		for (int i = 0; i < 16; i++) {
			mask = ByteUtils.set(mask, i, objects[i] != null
					&& (regardless || objects[i].isDirty()));
		}
		dos.writeShort(mask);
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null && (regardless || objects[i].isDirty())) {
				objects[i].writeToStream(dos, regardless);
				objects[i].resetChangeTimer(world);
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

	public void sync() {
		Set<EntityPlayer> players = getPlayersWatching();
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
						}
					} else {
						if (fullPacket == null) fullPacket = createPacket(true, false);
						OpenBlocks.proxy.sendPacketToPlayer((Player)player, fullPacket);
					}
				}
				knownUsers = newUsersInRange;
			} catch (IOException e) {
				Log.warn(e, "IOError during sync");
			}
		} else {
			try {
				OpenBlocks.proxy.sendPacketToServer(createPacket(false, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			knownUsers.clear();
		}
		markAllAsClean();
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
		writeToStream(getWorld(), bos, fullPacket);
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
