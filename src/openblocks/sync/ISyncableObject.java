package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public interface ISyncableObject {
	public boolean hasChanged();

	public void resetChangeStatus();

	public void setHasChanged();


	public void merge(ISyncableObject o);

	public void clear();
	
	public void registerTile(TileEntity tile);
	
	public void unregisterTile(TileEntity tile);

	public void readFromStream(DataInputStream stream) throws IOException;

	public void writeToStream(DataOutputStream stream) throws IOException;

	public void writeToNBT(NBTTagCompound tag, String name);

	public void readFromNBT(NBTTagCompound tag, String name);
	
	public long getUUID();

}
