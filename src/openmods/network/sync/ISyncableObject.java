package openmods.network.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface ISyncableObject {
	public boolean isDirty();

	public void markClean();

	public void markDirty();

	public void readFromStream(DataInput stream) throws IOException;

	public void writeToStream(DataOutput stream, boolean fullData) throws IOException;

	public void resetChangeTimer(World world);

	public int getTicksSinceChange(World world);

	public void writeToNBT(NBTTagCompound nbt, String name);

	public void readFromNBT(NBTTagCompound nbt, String name);
}
