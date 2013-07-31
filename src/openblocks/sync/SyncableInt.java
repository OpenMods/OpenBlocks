package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableInt extends SyncableObject implements ISyncableObject {

	public SyncableInt(int value) {
		super(value);
	}
	
	public SyncableInt() {
		this(0);
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = stream.readInt();
	}
	
	public void modify(int by) {
		int current = (Integer) getValue();
		current += by;
		setValue(current);
	}
	
	@Override
	public void merge(ISyncableObject o) {
		if (o instanceof SyncableInt) {
			modify((Integer)((SyncableInt) o).getValue());
			((SyncableInt) o).setValue(0);
		}
	}
	
	@Override
	public void clear() {
		value = 0;
	}
	
	public boolean equals(Object otherValue) {
		return ((Integer)value).intValue() == ((Integer)otherValue).intValue();
	}

	@Override
	public void writeToStream(DataOutputStream stream) throws IOException {
		stream.writeInt((Integer)value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setInteger(name, (Integer)value);
	}

	@Override
	public boolean readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getInteger(name);
			return true;
		}
		return false;
	}
}
