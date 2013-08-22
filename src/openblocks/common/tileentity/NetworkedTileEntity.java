package openblocks.common.tileentity;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
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
		super.sync();
		syncMap.sync(worldObj, this, (double)xCoord, (double)yCoord, (double)zCoord, timeout);
	}

	@Override
	public void sync() {
		sync(1);
	}

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

	@Override
	public Packet getDescriptionPacket() {
		return syncMap.getDescriptionPacket(this);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		syncMap.handleTileDataPacket(this, pkt);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
}
