package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableInt implements ISyncableObject {

	protected int value = 0;
	protected boolean dirty = false;
	protected int ticksSinceChange = 0;

	public SyncableInt(int value) {
		this.value = value;
	}

	public SyncableInt() {
		this(0);
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = stream.readInt();
	}

	public int getTicksSinceChange() {
		return ticksSinceChange;
	}

	public void modify(int by) {
		setValue(value + by);
	}

	public void setValue(int val) {
		if (val != value) {
			value = val;
			markDirty();
		}
	}

	public int getValue() {
		return value;
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData)
			throws IOException {
		stream.writeInt(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		tag.setInteger(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getInteger(name);
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void markClean() {
		dirty = false;
	}

	@Override
	public void markDirty() {
		dirty = true;
	}

	@Override
	public void resetChangeTimer() {
		ticksSinceChange = 0;
	}

	@Override
	public void tick() {
		ticksSinceChange++;
	}
}
