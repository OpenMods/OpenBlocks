package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class SyncableDirectionSet implements ISyncableObject {

	private Set<ForgeDirection> value = new HashSet<ForgeDirection>();
	private boolean hasChanged = false;

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public void resetChangeStatus() {
		hasChanged = false;
	}

	@Override
	public void setHasChanged() {
		hasChanged = true;
	}

	public Set<ForgeDirection> getValue() {
		return value;
	}

	public void addDirection(ForgeDirection direction) {
		if (!value.contains(direction)) {
			value.add(direction);
			setHasChanged();
		}
	}

	public void removeDirection(ForgeDirection direction) {
		if (value.contains(direction)) {
			value.remove(direction);
			setHasChanged();
		}
	}

	public void toggleDirection(ForgeDirection direction) {
		if (value.contains(direction)) {
			value.remove(direction);
		} else {
			value.add(direction);
		}
		setHasChanged();
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		int numberOfDirections = stream.readByte();
		value = new HashSet<ForgeDirection>();
		for (int i = 0; i < numberOfDirections; i++) {
			value.add(ForgeDirection.getOrientation(stream.readByte()));
		}
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData) throws IOException {
		stream.writeByte(value.size());
		Iterator<ForgeDirection> it = value.iterator();
		while (it.hasNext()) {
			ForgeDirection next = it.next();
			if (next != null) {
				stream.writeByte(next.ordinal());
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		int[] intarr = new int[value.size()];
		int i = 0;
		for (ForgeDirection dir : value) {
			intarr[i++] = dir.ordinal();
		}
		tag.setIntArray(name, intarr);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			int[] intarr = tag.getIntArray(name);
			value = new HashSet<ForgeDirection>();
			for (int element : intarr) {
				value.add(ForgeDirection.getOrientation(element));
			}
		}
	}

}
