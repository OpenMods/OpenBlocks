package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableFloat implements ISyncableObject {

	public static final float EPSILON = 0.0001f;
	private float value;
	private boolean hasChanged = false;
	private int ticksSinceChanged = 0;
	
	public SyncableFloat(float value) {
		this.value = value;
	}
	
	public SyncableFloat() {
		this(0.0f);
	}
	
	public void setValue(float newValue) {
		if (!equals(newValue)) {
			value = newValue;
			setHasChanged();
		}
	}
	
	public float getValue() {
		return value;
	}

	public boolean equals(float otherValue) {
		return Math.abs(otherValue - value) < EPSILON;
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = stream.readFloat();
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData) throws IOException {
		stream.writeFloat(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setFloat(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getFloat(name);
		}
	}

	public void modify(float by) {
		setValue(value + by);
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
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
