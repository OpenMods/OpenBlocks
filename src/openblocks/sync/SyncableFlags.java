package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.utils.ByteUtils;

public class SyncableFlags extends SyncableObjectBase {

	private short value;
	private short previousValue;
	protected long[] timeLastSet = new long[16];
	protected long[] timeLastUnset = new long[16];

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
				timeLastSet[slot] = 0;
			} else {
				timeLastUnset[slot] = 0;
			}
			markDirty();
		}
		value = newVal;
	}

	public int ticksSinceSet(World world, Enum<?> slot) {
		return ticksSinceSet(world, slot.ordinal());
	}

	public int ticksSinceSet(World world, int slot) {
		return (int)(OpenBlocks.proxy.getTicks(world) - timeLastSet[slot]);
	}

	public int ticksSinceUnset(World world, Enum<?> slot) {
		return ticksSinceUnset(world, slot.ordinal());
	}

	public int ticksSinceUnset(World world, int slot) {
		return (int)(OpenBlocks.proxy.getTicks(world) - timeLastUnset[slot]);
	}

	public boolean get(Enum<?> slot) {
		return get(slot.ordinal());
	}

	public boolean get(int slot) {
		return ByteUtils.get(value, slot);
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
	}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		value = stream.readShort();
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

	@Override
	public void resetChangeTimer(World world) {
		super.resetChangeTimer(world);
		long time = OpenBlocks.proxy.getTicks(world);
		for (int i = 0; i < timeLastSet.length; i++) {
			if (hasSlotChanged(i)) {
				if (get(i)) {
					timeLastSet[i] = time;
				}else {
					timeLastUnset[i] = time;
				}
			}
		}
	}
}
