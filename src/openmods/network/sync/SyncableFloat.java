package openmods.network.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableFloat extends SyncableObjectBase {

	public static final float EPSILON = 0.0001f;
	private float value;

	public SyncableFloat(float value) {
		this.value = value;
	}

	public SyncableFloat() {}

	public void setValue(float newValue) {
		if (!equals(newValue)) {
			value = newValue;
			markDirty();
		}
	}

	public float getValue() {
		return value;
	}

	public boolean equals(float otherValue) {
		return Math.abs(otherValue - value) < EPSILON;
	}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		value = stream.readFloat();
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData) throws IOException {
		stream.writeFloat(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setFloat(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		value = tag.getFloat(name);
	}

	public void modify(float by) {
		setValue(value + by);
	}
}
