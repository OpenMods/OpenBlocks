package openmods.network.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

import com.google.common.primitives.SignedBytes;

public class SyncableByte extends SyncableObjectBase {

	private byte value;

	public SyncableByte(byte value) {
		this.value = value;
	}

	public SyncableByte() {}

	public void setValue(byte newValue) {
		if (newValue != value) {
			value = newValue;
			markDirty();
		}
	}

	public byte getValue() {
		return value;
	}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		value = stream.readByte();
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData) throws IOException {
		stream.writeByte(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setByte(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		value = tag.getByte(name);
	}

	public void modify(int by) {
		setValue(SignedBytes.checkedCast(value + by));
	}
}
