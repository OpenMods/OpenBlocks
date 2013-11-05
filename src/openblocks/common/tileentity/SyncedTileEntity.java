package openblocks.common.tileentity;

import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Log;
import openblocks.common.block.OpenBlock;
import openblocks.sync.*;

public abstract class SyncedTileEntity extends OpenTileEntity implements
		ISyncHandler {

	
	protected SyncMapTile<SyncedTileEntity> syncMap;

	public SyncedTileEntity() {
		syncMap = new SyncMapTile<SyncedTileEntity>(this);
	}
	
	@Override
	public void setup() {
		OpenBlock block = getBlock();
		for (Field field : block.getSyncedFields()) {
			try {
				addSyncedObject(field.getName(), (ISyncableObject)field.get(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addSyncedObject(String name, ISyncableObject obj) {
		syncMap.put(name, obj);
	}

	public void sync() {
		syncMap.sync();
	}

	@Override
	public SyncMap<SyncedTileEntity> getSyncMap() {
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
		ISyncableObject rot = syncMap.get("_rotation2");
		if (rot != null) {
			return ((SyncableDirection) rot).getValue();
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
