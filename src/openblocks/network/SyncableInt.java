package openblocks.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableInt extends SyncableObject implements ISyncableObject {

	public SyncableInt(int value) {
		super(value);
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = stream.readInt();
	}

	@Override
	public void writeToStream(DataOutputStream stream) throws IOException {
		stream.writeInt((Integer) value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setInteger(name, (Integer) value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getInteger(name);
		}
	}
}
