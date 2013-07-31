package openblocks.sync;

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

	public boolean equals(Object otherValue) {
		return Arrays.equals((int[])this.value, (int[])otherValue);
	}
	
	public int size() {
		if (value == null) {
			return 0;
		}
		return ((int[]) value).length;
	}

	public boolean isEmpty() {
		return size() == 0;
	}
	
	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		int length = stream.readInt();
		value = new int[length];
		for (int i = 0; i < length; i++) {
			((int[])value)[i] = stream.readInt();
		}
	}

	@Override
	public void writeToStream(DataOutputStream stream) throws IOException {
		int length = ((int[])value).length;
		stream.writeInt(length);
		for (int i = 0; i < length; i++) {
			stream.writeInt(((int[])value)[i]);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setIntArray(name, (int[])value);
	}

	@Override
	public boolean readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getIntArray(name);
			return true;
		}
		return false;
	}

	public void clear() {
		value = new int[0];
		hasChanged = true;
	}
	
	public void merge(ISyncableObject o) {
		
	}
}
