package openperipheral.api;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableInt {

	protected String name;
	protected int value;

	public SyncableInt(String name) {
		this.name = name;
	}

	public SyncableInt(String name, int value) {
		this(name);
		this.value = value;
	}

	public void add(int val) {
		value += val;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public void readFromNBT(NBTTagCompound tag) {
		if (tag.hasKey(name)) {
			value = tag.getInteger(name);
		}
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger(name, value);
	}

	public boolean is(int mask) {
		return (value & mask) == mask;
	}

	public void toggle(int mask) {
		if (is(mask)) {
			value &= ~mask;
		} else {
			value |= mask;
		}
	}
}
