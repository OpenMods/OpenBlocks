package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableShort implements ISyncableObject {

	private short value = 0;
	private boolean hasChanged = false;
	private int ticksSinceChanged = 0;
	
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
		setValue((short) (value + by));
	}

	public void setValue(short val) {
		if (val != value) {
			value = val;
			setHasChanged();
		}
	}
	
	public short getValue() {
		return value;
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData) throws IOException {
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

	public boolean hasChanged() {
		return hasChanged;
	}

	public void resetChangeStatus() {
		hasChanged = false;
		ticksSinceChanged++;
	}

	@Override
	public void setHasChanged() {
		hasChanged = true;
		ticksSinceChanged = 0;
	}
}
