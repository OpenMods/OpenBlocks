package openblocks.sync;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.utils.ByteUtils;

public abstract class SyncMap {

	private int trackingRange = 20;

	public SyncMap() {}

	public SyncMap(int trackingRange) {
		this.trackingRange = trackingRange;
	}

	public List<Integer> usersInRange = new ArrayList<Integer>();

	private ISyncableObject[] objects = new ISyncableObject[16];

	public void put(Enum id, ISyncableObject value) {
		put(id.ordinal(), value);
	}

	public void put(int id, ISyncableObject value) {
		objects[id] = value;
	}

	public List<ISyncableObject> readFromStream(DataInputStream dis)
			throws IOException {
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

	public void writeToStream(DataOutputStream dos, boolean regardless)
			throws IOException {
		short mask = 0;
		for (int i = 0; i < 16; i++) {
			mask = ByteUtils.set(mask, i, objects[i] != null
					&& (regardless || objects[i].hasChanged()));
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

	public void sync(World worldObj, ISyncHandler handler, double x, double y, double z) {
		if (!worldObj.isRemote) {
			List<EntityPlayer> players = (List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(trackingRange, trackingRange, trackingRange));
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
					List<Integer> newUsersInRange = new ArrayList<Integer>();
					for (EntityPlayer player : players) {
						newUsersInRange.add(player.entityId);
						if (player != null) {
							Packet packetToSend = null;
							if (usersInRange.contains(player.entityId)) {
								if (hasChanges) {
									if (changePacket == null) {
										changePacket = createPacket(handler, false);
									}
									packetToSend = changePacket;
								}
							} else {
								if (fullPacket == null) {
									fullPacket = createPacket(handler, true);
								}
								packetToSend = fullPacket;
							}
							if (packetToSend != null) {
								((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packetToSend);
							}
						}
						usersInRange = newUsersInRange;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		resetChangeStatus();
	}

	protected Packet createPacket(ISyncHandler handler, boolean fullPacket)
			throws IOException {
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

	protected abstract void writeMapType(DataOutputStream dos)
			throws IOException;
}
