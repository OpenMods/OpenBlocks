package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableDouble implements ISyncableObject {

	private double value;
	private boolean hasChanged = false;
	private int ticksSinceChanged = 0;
	
	public SyncableDouble(double value) {
		this.value = value;
	}
	
	public SyncableDouble() {
		this(0.0f);
	}
	
	public void setValue(double newValue) {
		if (newValue != value) {
			value = newValue;
			setHasChanged();
		}
	}
	
	public double getValue() {
		return value;
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = stream.readDouble();
	}

	@Override
	public void writeToStream(DataOutputStream stream) throws IOException {
		stream.writeDouble(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setDouble(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getDouble(name);
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
