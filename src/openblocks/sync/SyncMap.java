package openblocks.sync;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.network.PacketHandler;
import openblocks.utils.ByteUtils;

import com.google.common.collect.Sets;

public abstract class SyncMap {

	public Set<Integer> usersInRange = new HashSet<Integer>();

	private ISyncableObject[] objects = new ISyncableObject[16];

	private int index = 0;

	public void put(ISyncableObject value) {
		objects[index++] = value;
	}

	public ISyncableObject get(Enum<?> id) {
		return get(id.ordinal());
	}

	public ISyncableObject get(int id) {
		return objects[id];
	}

	public List<ISyncableObject> readFromStream(DataInputStream dis)
			throws IOException {
		short mask = dis.readShort();
		List<ISyncableObject> changes = new ArrayList<ISyncableObject>();
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null) {
				if (ByteUtils.get(mask, i)) {
					objects[i].readFromStream(dis);
					changes.add(objects[i]);
					objects[i].resetChangeTimer();
				}else {
					objects[i].tick();
				}
			}
		}
		return changes;
	}

	public void writeToStream(DataOutputStream dos, boolean regardless)
			throws IOException {
		short mask = 0;
		for (int i = 0; i < 16; i++) {
			mask = ByteUtils.set(mask, i, objects[i] != null
					&& (regardless || objects[i].isDirty()));
		}
		dos.writeShort(mask);
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null && (regardless || objects[i].isDirty())) {
				objects[i].writeToStream(dos, regardless);
			}
		}
	}

	public void markAllAsClean() {
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null) {
				objects[i].tick();
				objects[i].markClean();
			}
		}
	}

	/***
	 * Sync the changed values
	 * 
	 * @param worldObj
	 * @param handler
	 * @param x
	 * @param y
	 * @param z
	 */
	public void sync(World worldObj, ISyncHandler handler, double x, double y, double z) {
		if (worldObj instanceof WorldServer) {
			Set<EntityPlayer> players = PacketHandler.getPlayersWatchingBlock((WorldServer)worldObj, (int)x, (int)z);
			if (!players.isEmpty()) {
				Packet changePacket = null;
				Packet fullPacket = null;

				boolean hasChanges = hasChanges();
				try {
					Set<Integer> newUsersInRange = Sets.newHashSet();
					for (EntityPlayer player : players) {
						newUsersInRange.add(player.entityId);
						if (usersInRange.contains(player.entityId)) {
							if (hasChanges) {
								if (changePacket == null) changePacket = createPacket(handler, false, false);
								PacketHandler.sendPacketToPlayer(player, changePacket);
							}
						} else {
							if (fullPacket == null) fullPacket = createPacket(handler, true, false);
							PacketHandler.sendPacketToPlayer(player, fullPacket);
						}
					}
					usersInRange = newUsersInRange;
				} catch (IOException e) {
					Log.warn(e, "IOError during sync");
				}
			} else {
				usersInRange.clear();
			}
		} else {
			try {
				PacketHandler.sendPacketToServer(createPacket(handler, false, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		markAllAsClean();
	}

	private boolean hasChanges() {
		for (ISyncableObject obj : objects) {
			if (obj != null && obj.isDirty()) return true;
		}

		return false;
	}

	protected Packet createPacket(ISyncHandler handler, boolean fullPacket, boolean toServer)
			throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		outputStream.writeByte(toServer? 1 : 0);
		writeMapType(outputStream);
		handler.writeIdentifier(outputStream);
		writeToStream(outputStream, fullPacket);
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = OpenBlocks.CHANNEL;
		packet.data = bos.toByteArray();
		packet.length = packet.data.length;
		return packet;
	}

	protected abstract void writeMapType(DataOutputStream dos)
			throws IOException;
}
