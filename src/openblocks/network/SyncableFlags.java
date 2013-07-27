package openblocks.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;

import openblocks.utils.ByteUtils;

public class SyncableFlags implements ISyncableObject {
	
	private short value;
	private boolean hasChanged = false;
	
	public SyncableFlags() {
		
	}
	
	public void on(int slot) {
		set(slot, true);
	}

	public void off(int slot) {
		set(slot, false);
	}
	
	public void set(int slot, boolean bool) {
		short newVal = ByteUtils.set(value, slot, bool);
		if (newVal != value) {
			hasChanged = true;
		}
		value = newVal;
	}
	
	public boolean get(int slot) {
		return ByteUtils.get(value, slot);
	}
	
	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public void resetChangeStatus() {
		hasChanged = false;
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = stream.readShort();
	}

	@Override
	public void writeToStream(DataOutputStream stream) throws IOException {
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

}
