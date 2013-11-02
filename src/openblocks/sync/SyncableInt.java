package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableInt extends SyncableObjectBase {

	protected int value = 0;

	public SyncableInt(int value) {
		this.value = value;
	}

	public SyncableInt() {}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		value = stream.readInt();
	}

	public void modify(int by) {
		setValue(value + by);
	}

	public void setValue(int val) {
		if (val != value) {
			value = val;
			markDirty();
		}
	}

	public int getValue() {
		return value;
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData) throws IOException {
		stream.writeInt(value);
	}

	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setInteger(name, value);
	}

	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getInteger(name);
		}
	}

}
