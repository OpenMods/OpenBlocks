package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import openblocks.utils.ByteUtils;

public class SyncableFlags implements ISyncableObject {

	private short value;
	private short previousValue;
	private boolean dirty = false;
	protected int[] ticksSinceSet = new int[16];
	protected int[] ticksSinceUnset = new int[16];
	protected int ticksSinceChanged = 0;

	public SyncableFlags() {}

	public void on(Enum<?> slot) {
		on(slot.ordinal());
	}

	public void on(int slot) {
		set(slot, true);
	}

	public void off(Enum<?> slot) {
		off(slot.ordinal());
	}

	public void off(int slot) {
		set(slot, false);
	}

	public void set(Enum<?> slot, boolean bool) {
		set(slot.ordinal(), bool);
	}

	public void toggle(int slot) {
		set(slot, !get(slot));
	}

	public void toggle(Enum<?> slot) {
		toggle(slot.ordinal());
	}

	public Set<Integer> getActiveSlots() {
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < 16; i++) {
			if (get(i)) {
				set.add(i);
			}
		}
		return set;
	}

	public void set(int slot, boolean bool) {
		short newVal = ByteUtils.set(value, slot, bool);
		if (newVal != value) {
			if (bool) {
				ticksSinceSet[slot] = 0;
			} else {
				ticksSinceUnset[slot] = 0;
			}
			markDirty();
		}
		value = newVal;
	}

	@Override
	public void markDirty() {
		dirty = true;
	}

	public int ticksSinceSet(Enum<?> slot) {
		return ticksSinceSet(slot.ordinal());
	}

	public int ticksSinceSet(int slot) {
		return ticksSinceSet[slot];
	}

	public int ticksSinceUnset(Enum<?> slot) {
		return ticksSinceUnset(slot.ordinal());
	}

	public int ticksSinceUnset(int slot) {
		return ticksSinceUnset[slot];
	}

	public int ticksSinceChange() {
		return ticksSinceChanged;
	}

	public boolean get(Enum<?> slot) {
		return get(slot.ordinal());
	}

	public boolean get(int slot) {
		return ByteUtils.get(value, slot);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	public boolean hasSlotChanged(Enum<?> slot) {
		return hasSlotChanged(slot.ordinal());
	}

	public boolean hasSlotChanged(int slot) {
		return ByteUtils.get(value, slot) != ByteUtils.get(previousValue, slot);
	}

	@Override
	public void markClean() {
		previousValue = value;
		dirty = false;
		for (int i = 0; i < ticksSinceSet.length; i++) {
			ticksSinceSet[i]++;
			ticksSinceUnset[i]++;
		}
		ticksSinceChanged++;
	}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		value = stream.readShort();
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData) throws IOException {
		stream.writeShort(value);
	}

	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setShort(name, value);
	}

	public void readFromNBT(NBTTagCompound tag, String name) {
		value = tag.getShort(name);
	}

	@Override
	public void resetChangeTimer() {
		ticksSinceChanged = 0;
	}
}
