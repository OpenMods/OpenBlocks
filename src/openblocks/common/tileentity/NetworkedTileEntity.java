package openblocks.common.tileentity;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Log;
import openblocks.sync.*;

public abstract class NetworkedTileEntity extends OpenTileEntity implements
		ISyncHandler {

	protected SyncMapTile<NetworkedTileEntity> syncMap = new SyncMapTile<NetworkedTileEntity>(this);

	private int specialObjectIndex = -1;
	
	public void addSyncedObject(ISyncableObject obj) {
		syncMap.put(obj);
	}
	
	/**
	 * Used by OpenBlocks for tiles that have 24 rotations. Don't use this! thx
	 * @param obj
	 */
	public void addSpecialObject(ISyncableObject obj) {
		if (specialObjectIndex == -1) {
			specialObjectIndex = syncMap.size();
			syncMap.put(obj);	
		}
	}
	
	public ISyncableObject getSpecialObject() {
		if (specialObjectIndex == -1) {
			return null;
		}
		return syncMap.get(specialObjectIndex);
	}

	public void sync() {
		syncMap.sync();
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


	public ForgeDirection getSecondaryRotation() {
		ISyncableObject obj = getSpecialObject();
		if (obj instanceof SyncableDirection) {
			return ((SyncableDirection)obj).getValue();
		}
		return null;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		syncMap.writeToNBT(tag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		syncMap.readFromNBT(tag);
	}
}
