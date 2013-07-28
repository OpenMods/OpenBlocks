package openblocks.network;

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
import openblocks.utils.ByteUtils;

public class SyncMap {
	
	private List<Integer> usersInRange = new ArrayList<Integer>();
	
	private ISyncableObject[] objects = new ISyncableObject[16];
	
	public SyncMap() {
	}
	
	public void put(int id, ISyncableObject value) {
		objects[id] = value;
	}
	
	public List<ISyncableObject> readFromStream(DataInputStream dis) throws IOException {
		short mask = dis.readShort();
		List<ISyncableObject> changes = new ArrayList<ISyncableObject>();
		for (int i = 0; i < 16; i++) {
			if (ByteUtils.get(mask, i) && objects[i] != null) {
				objects[i].readFromStream(dis);
				changes.add(objects[i]);
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
				objects[i].writeToStream(dos);
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
	
	public void syncNearbyUsers(TileEntity tile) {
		List<EntityPlayer> players = (List<EntityPlayer>)tile.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(tile.xCoord, tile.yCoord, tile.zCoord, tile.xCoord+ 1, tile.yCoord+ 1,  tile.zCoord + 1).expand(20, 20, 20));
		if (players.size() > 0) {
			Packet changePacket = null;
			Packet fullPacket = null;
			try {
				List<Integer> newUsersInRange = new ArrayList<Integer>();
				for (EntityPlayer player : players) {
					newUsersInRange.add(player.entityId);
					if (player != null) {
						Packet packetToSend = null;
						if (usersInRange.contains(player.entityId)) {
							if (changePacket == null) {
								changePacket = createPacket(tile.xCoord, tile.yCoord, tile.zCoord, false);
							}
							packetToSend = changePacket;
						}else {
							if (fullPacket == null) {
								fullPacket = createPacket(tile.xCoord, tile.yCoord, tile.zCoord, true);
							}
							packetToSend = fullPacket;
						}
						((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packetToSend);
					}
					usersInRange = newUsersInRange;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Packet createPacket(int x, int y, int z, boolean fullPacket) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		outputStream.writeInt(x);
		outputStream.writeInt(y);
		outputStream.writeInt(z);
		writeToStream(outputStream, fullPacket);
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "OpenBlocks";
		packet.data = bos.toByteArray();
		packet.length = packet.data.length;
		return packet;
	}
}
