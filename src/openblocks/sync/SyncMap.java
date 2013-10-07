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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.network.PacketHandler;
import openblocks.utils.ByteUtils;

public abstract class SyncMap {

	private int trackingRange = 64;
	private long totalTrackingTime = 0;

	public SyncMap() {}

	public SyncMap(int trackingRange) {
		this.trackingRange = trackingRange;
	}

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

	/*
	 * By registering each sync map statically with a primary sync manager, and
	 * then running that at the end of each tick. We could spare a lot of
	 * processing time and resources Just saying -NeverCast :)
	 */

	public void sync(World worldObj, ISyncHandler handler, double x, double y, double z) {
		sync(worldObj, handler, x, y, z, 20);
	}
	
	public Set<EntityPlayer> getListeningPlayers(World worldObj, double x, double z, int trackingRange) {
		return PacketHandler.getPlayersInRange(worldObj, (int)x, (int)z, trackingRange);
	}

	public void sync(World worldObj, ISyncHandler handler, double x, double y, double z, int tickUpdatePeriod) {
		if (!worldObj.isRemote) {
			// TODO: Test the shit out of this.
			long worldTotalTime = OpenBlocks.proxy.getTicks(worldObj);
			if (totalTrackingTime == 0) totalTrackingTime = worldTotalTime;
			if (worldTotalTime - totalTrackingTime < tickUpdatePeriod) return;
			totalTrackingTime = worldTotalTime; // Out with the old
			Set<EntityPlayer> players = getListeningPlayers(worldObj, x, z, trackingRange);

			if (players.size() > 0) {
				Packet changePacket = null;
				Packet fullPacket = null;

				boolean hasChanges = false;

				for (ISyncableObject obj : objects) {
					if (obj != null && obj.hasChanged()) {
						hasChanges = true;
						break;
					}
				}
				try {
					Set<Integer> newUsersInRange = new HashSet<Integer>();
					for (EntityPlayer player : players) {
						newUsersInRange.add(player.entityId);
						if (player != null) {
							Packet packetToSend = null;
							if (usersInRange.contains(player.entityId)) {
								if (hasChanges) {
									if (changePacket == null) {
										// System.out.println("Creating change packet");
										changePacket = createPacket(handler, false);
									}
									packetToSend = changePacket;
								}
							} else {
								if (fullPacket == null) {
									// System.out.println("Creating full packet");
									fullPacket = createPacket(handler, true);
								}
								packetToSend = fullPacket;
							}
							if (packetToSend != null) {
								((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packetToSend);
							}
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
