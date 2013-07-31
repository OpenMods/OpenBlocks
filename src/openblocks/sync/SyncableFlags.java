package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import openblocks.utils.ByteUtils;

public class SyncableFlags implements ISyncableObject {

	private short value;
	private short previousValue;
	private boolean hasChanged = false;

	protected int[] ticksSinceSet = new int[16];
	protected int[] ticksSinceUnset = new int[16];
	protected int ticksSinceChanged = 0;
	private UUID id = null;
	public WeakHashMap<TileEntity, Void> tiles;

	public SyncableFlags() {

	}

	public void setOwner(TileEntity o) {
		tiles = new WeakHashMap<TileEntity, Void>();
	}

	public void on(Enum slot) {
		on(slot.ordinal());
	}

	public void on(int slot) {
		set(slot, true);
	}

	public void off(Enum slot) {
		off(slot.ordinal());
	}

	public void off(int slot) {
		set(slot, false);
	}

	public void set(Enum slot, boolean bool) {
		set(slot.ordinal(), bool);
	}

	public void set(int slot, boolean bool) {
		short newVal = ByteUtils.set(value, slot, bool);
		if (newVal != value) {
			if (bool) {
				ticksSinceSet[slot] = 0;
			} else {
				ticksSinceUnset[slot] = 0;
			}
			setHasChanged();
		}
		value = newVal;
	}

	public void setHasChanged() {
		hasChanged = true;
		ticksSinceChanged = 0;
	}

	public int ticksSinceSet(Enum slot) {
		return ticksSinceSet(slot.ordinal());
	}

	public int ticksSinceSet(int slot) {
		return ticksSinceSet[slot];
	}

	public int ticksSinceUnset(Enum slot) {
		return ticksSinceUnset(slot.ordinal());
	}

	public int ticksSinceUnset(int slot) {
		return ticksSinceUnset[slot];
	}

	public int ticksSinceChange(Enum slot) {
		return ticksSinceChange(slot.ordinal());
	}

	public int ticksSinceChange(int slot) {
		return ticksSinceChanged;
	}

	public boolean get(Enum slot) {
		return get(slot.ordinal());
	}

	public boolean get(int slot) {
		return ByteUtils.get(value, slot);
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	public boolean hasSlotChanged(Enum slot) {
		return hasSlotChanged(slot.ordinal());
	}

	public boolean hasSlotChanged(int slot) {
		return ByteUtils.get(value, slot) != ByteUtils.get(previousValue, slot);
	}

	@Override
	public void resetChangeStatus() {
		previousValue = value;
		hasChanged = false;
		for (int i = 0; i < ticksSinceSet.length; i++) {
			ticksSinceSet[i]++;
			ticksSinceUnset[i]++;
		}
		ticksSinceChanged++;
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
	public boolean readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getShort(name);
			return true;
		}
		return false;
	}

	@Override
	public void registerTile(TileEntity tile) {
		tiles.put(tile, null);
	}

	@Override
	public void unregisterTile(TileEntity tile) {
		tiles.remove(tile);
	}

	@Override
	public void clear() {
		value = 0;
	}

	@Override
	public void merge(ISyncableObject o) {
		// TODO Auto-generated method stub

	}

}
