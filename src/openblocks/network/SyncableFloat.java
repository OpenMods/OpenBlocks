package openblocks.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableFloat extends SyncableObject implements ISyncableObject {

	public static final float EPSILON = 0.0001f;

	public SyncableFloat(float value) {
		super(value);
	}

	@Override
	public boolean equals(Object otherValue) {
		return Math.abs((Float)otherValue - (Float)value) < EPSILON;
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = stream.readFloat();
	}

	@Override
	public void writeToStream(DataOutputStream stream) throws IOException {
		stream.writeFloat((Float)value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setFloat(name, (Float)value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getFloat(name);
		}
	}
}
