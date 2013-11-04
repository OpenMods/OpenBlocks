package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableShort extends SyncableObjectBase {

	private short value = 0;

	public SyncableShort(short value) {
		this.value = value;
	}

	public SyncableShort() {}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
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
	public void writeToStream(DataOutput stream, boolean fullData) throws IOException {
		stream.writeShort(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setShort(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		value = tag.getShort(name);
	}
}
