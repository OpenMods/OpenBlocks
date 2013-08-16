package openblocks.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import openblocks.OpenBlocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;

public class SyncMapTile extends SyncMap {

	@Override
	protected void writeMapType(DataOutputStream dos) throws IOException {
		dos.writeByte(SyncableManager.TYPE_TILE);
	}
	
	public void handleTileDataPacket(ISyncHandler handler, Packet132TileEntityData packet) {
		if(packet.actionType == 0) {
			NBTTagCompound compound = packet.customParam1;
			if(compound != null && compound.hasKey("payload")) {
				byte[] payload = compound.getByteArray("payload");
				if(payload != null) {
					Packet250CustomPayload custom = new Packet250CustomPayload("OpenBlocks", payload);
					try {
						OpenBlocks.syncableManager.handlePacket(custom);
					} catch (IOException e) {
					}
				}
			}
		}
	}

}
