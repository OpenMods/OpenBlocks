package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableShort implements ISyncableObject {

	private short value = 0;
	private boolean dirty = false;

	public SyncableShort(short value) {
		this.value = value;
	}

	public SyncableShort() {
		this((short)0);
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = stream.readShort();
	}

	public void modify(short by) {
		setValue((short)(value + by));
	}

	public void setValue(short val) {
		if (val != value) {
			value = val;
			markDirty();
		}
	}

	public short getValue() {
		return value;
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData)
			throws IOException {
		stream.writeShort(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setShort(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getShort(name);
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void markClean() {
		dirty = false;
	}

	@Override
	public void markDirty() {
		dirty = true;
	}

	@Override
	public void resetChangeTimer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
}
