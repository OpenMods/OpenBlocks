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
import openblocks.network.PacketHandler;
import openblocks.utils.ByteUtils;

import com.google.common.collect.Sets;

public abstract class SyncMap {

	public Set<Integer> usersInRange = new HashSet<Integer>();

	private ISyncableObject[] objects = new ISyncableObject[16];

	public void put(Enum<?> id, ISyncableObject value) {
		put(id.ordinal(), value);
	}

	public void put(int id, ISyncableObject value) {
		objects[id] = value;
	}

	public ISyncableObject get(Enum<?> id) {
		return get(id.ordinal());
	}

	public ISyncableObject get(int id) {
		return objects[id];
	}

	public List<ISyncableObject> readFromStream(DataInputStream dis) throws IOException {
		short mask = dis.readShort();
		List<ISyncableObject> changes = new ArrayList<ISyncableObject>();
		for (int i = 0; i < 16; i++) {
			if (ByteUtils.get(mask, i) && objects[i] != null) {
				objects[i].readFromStream(dis);
				changes.add(objects[i]);
				objects[i].setHasChanged();
			}
		}
		return changes;
	}

	public void writeToStream(DataOutputStream dos, boolean regardless) throws IOException {
		short mask = 0;
		for (int i = 0; i < 16; i++) {
			mask = ByteUtils.set(mask, i, objects[i] != null && (regardless || objects[i].hasChanged()));
		}
		dos.writeShort(mask);
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null && (regardless || objects[i].hasChanged())) {
				objects[i].writeToStream(dos, regardless);
			}
		}
	}

	public void resetChangeStatus() {
		for (int i = 0; i < 16; i++) {
			if (objects[i] != null) {
				objects[i].resetChangeStatus();
			}
		}
	}

	/***
	 * Sync the changed values
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
								if (changePacket == null) changePacket = createPacket(handler, false);
								PacketHandler.sendPacketToPlayer(player, changePacket);
							}
						} else {
							if (fullPacket == null) fullPacket = createPacket(handler, true);
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
		}
		resetChangeStatus();
	}

	private boolean hasChanges() {
		for (ISyncableObject obj : objects) {
			if (obj != null && obj.hasChanged()) return true;
		}

		return false;
	}

	protected Packet createPacket(ISyncHandler handler, boolean fullPacket) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		writeMapType(outputStream);
		handler.writeIdentifier(outputStream);
		writeToStream(outputStream, fullPacket);
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "OpenBlocks";
		packet.data = bos.toByteArray();
		packet.length = packet.data.length;
		return packet;
	}

	protected abstract void writeMapType(DataOutputStream dos) throws IOException;
}
