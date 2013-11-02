package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableIntArray implements ISyncableObject {

	private int[] value;
	private boolean dirty = false;

	public SyncableIntArray(int[] value) {
		this.value = value;
	}

	public SyncableIntArray() {
		this(new int[0]);
	}

	public void setValue(int[] newValue) {
		if (!Arrays.equals(value, newValue)) {
			value = newValue;
			markDirty();
		}
	}

	public int[] getValue() {
		return value;
	}

	public int size() {
		if (value == null) { return 0; }
		return value.length;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		int length = stream.readInt();
		value = new int[length];
		for (int i = 0; i < length; i++) {
			value[i] = stream.readInt();
		}
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData)
			throws IOException {
		stream.writeInt(size());
		for (int i = 0; i < size(); i++) {
			stream.writeInt(value[i]);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setIntArray(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getIntArray(name);
		}
	}

	public void clear() {
		value = new int[0];
		dirty = true;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public void markDirty() {
		dirty = true;
	}

	@Override
	public void resetChangeTimer() {

	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markClean() {
		dirty = false;
	}
}
