package openblocks.common.tileentity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapTile;

public abstract class NetworkedTileEntity extends OpenTileEntity implements
		ISyncHandler {

	protected SyncMapTile syncMap = new SyncMapTile();

	public void addSyncedObject(Enum key, ISyncableObject obj) {
		syncMap.put(key, obj);
	}

	public void sync(int timeout) {
		syncMap.sync(worldObj, this, (double)xCoord, (double)yCoord, (double)zCoord, timeout);
	}

	public void sync() {
		sync(1);
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {}

	@Override
	public void writeIdentifier(DataOutputStream dos) throws IOException {
		dos.writeInt(xCoord);
		dos.writeInt(yCoord);
		dos.writeInt(zCoord);
	}

	@Override
	public SyncMap getSyncMap() {
		return syncMap;
	}
}
