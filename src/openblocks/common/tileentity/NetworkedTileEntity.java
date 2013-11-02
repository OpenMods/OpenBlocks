package openblocks.common.tileentity;

import java.io.IOException;

import net.minecraft.network.packet.Packet;
import openblocks.Log;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapTile;

public abstract class NetworkedTileEntity extends OpenTileEntity implements
		ISyncHandler {

	protected SyncMapTile<NetworkedTileEntity> syncMap = new SyncMapTile<NetworkedTileEntity>(this);

	public void addSyncedObject(ISyncableObject obj) {
		syncMap.put(obj);
	}

	public void sync(boolean syncMeta) {
		if (syncMeta) super.sync();
		syncMap.sync();
	}

	@Override
	public void sync() {
		sync(true);
	}

	@Override
	public SyncMap<NetworkedTileEntity> getSyncMap() {
		return syncMap;
	}

	@Override
	public Packet getDescriptionPacket() {
		try {
			return syncMap.createPacket(true, false);
		} catch (IOException e) {
			Log.severe(e, "Error during description packet creation");
			return null;
		}
	}
}
