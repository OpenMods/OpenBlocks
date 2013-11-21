package openmods.network.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableBoolean extends SyncableObjectBase {

	private boolean value;

	public SyncableBoolean(boolean value) {
		this.value = value;
	}

	public SyncableBoolean() {}

	public void setValue(boolean newValue) {
		if (newValue != value) {
			value = newValue;
			markDirty();
		}
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		value = stream.readBoolean();
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData) throws IOException {
		stream.writeBoolean(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setBoolean(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		value = tag.getBoolean(name);
	}

	public void toggle() {
		value = !value;
		markDirty();
	}
}
