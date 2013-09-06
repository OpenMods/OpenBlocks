package openblocks.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import openblocks.Log;
import openblocks.OpenBlocks;

public class SyncMapTile extends SyncMap {

	@Override
	protected void writeMapType(DataOutputStream dos) throws IOException {
		dos.writeByte(SyncableManager.TYPE_TILE);
	}

	/*
	 * Dirty wrapper for 250 packets to be sent with Chunk data. We should
	 * really do this properly.. one day
	 */
	public Packet getDescriptionPacket(ISyncHandler handler) {
		try {
			// Tile Entities only
			if (!(handler instanceof TileEntity)) return null; 
			TileEntity ent = (TileEntity)handler;
			Packet250CustomPayload packet250 = (Packet250CustomPayload)createPacket(handler, true);
			/* We now turn it in to a TileEntityUpdate packet */
			NBTTagCompound extraData = new NBTTagCompound();
			extraData.setByteArray("payload", packet250.data);
			Packet132TileEntityData tileEntityDataPacket = new Packet132TileEntityData(ent.xCoord, ent.yCoord, ent.zCoord, 0, extraData);
			return tileEntityDataPacket;
		} catch (Exception ex) {
			Log.warn(ex, "Error during packet 250 building");
			return null;
		}
	}

	public void handleTileDataPacket(ISyncHandler handler, Packet132TileEntityData packet) {
		if (packet.actionType == 0) {
			NBTTagCompound compound = packet.customParam1;
			if (compound != null && compound.hasKey("payload")) {
				byte[] payload = compound.getByteArray("payload");
				if (payload != null) {
					Packet250CustomPayload custom = new Packet250CustomPayload("OpenBlocks", payload);
					try {
						OpenBlocks.syncableManager.handlePacket(custom);
					} catch (IOException e) {
						Log.warn(e, "IOError while handling packet data");
					}
				}
			}
		}
	}

}
