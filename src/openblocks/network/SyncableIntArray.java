package openblocks.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableIntArray extends SyncableObject implements ISyncableObject {

	public SyncableIntArray(Object value) {
		super(value);
	}
	
	public SyncableIntArray() {
		super(new int[0]);
	}

	public void setValue(Object value) {
		if (!Arrays.equals((int[])this.value, (int[])value)) {
			hasChanged = true;
			this.value = value;
		}
	}

	public boolean isEmpty() {
		return value == null || ((int[]) value).length == 0;
	}
	
	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		int length = stream.readInt();
		value = new int[length];
		for (int i = 0; i < length; i++) {
			((Integer[])value)[i] = stream.readInt();
		}
	}

	@Override
	public void writeToStream(DataOutputStream stream) throws IOException {
		int length = ((int[])value).length;
		stream.writeInt(length);
		for (int i = 0; i < length; i++) {
			stream.writeInt(((Integer[])value)[i]);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setIntArray(name, (int[])value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		value = tag.getIntArray(name);
	}

	public void clear() {
		value = new int[0];
	}
	
}
