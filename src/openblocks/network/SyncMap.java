package openblocks.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import openblocks.utils.ByteUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class SyncMap {
	
	private ArrayList<String> usersInRange = new ArrayList<String>();
	
	private int id = -1;
	private ISyncableObject[] objects = new ISyncableObject[16];
	
	public SyncMap() {
	}
	
	public void put(int id, ISyncableObject value) {
		objects[id] = value;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void writeToNetwork(NBTTagCompound tag) {
		tag.setInteger("syncId", id);
	}

	public void readFromNetwork(NBTTagCompound tag) {
		if (tag.hasKey("syncId")) {
			id = tag.getInteger("syncId");
		}
	}

	public void readFromStream(DataInputStream dis) throws IOException {
		short mask = dis.readShort();
		for (int i = 0; i < 16; i++) {
			if (ByteUtils.get(mask, i) && objects[i] != null) {
				objects[i].readFromStream(dis);
			}
		}
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
			Packet packet;
			try {
				packet = createFullPacket();
				for (EntityPlayer player : players) {
					if (player != null) {
						((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Packet createFullPacket() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		outputStream.writeShort(getId());
		writeToStream(outputStream, true);
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "OpenBlocks";
		packet.data = bos.toByteArray();
		packet.length = packet.data.length;
		return packet;
	}
}
