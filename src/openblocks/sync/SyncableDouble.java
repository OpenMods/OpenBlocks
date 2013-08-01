package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class SyncableDouble extends SyncableObject implements ISyncableObject {

	public SyncableDouble(Double value) {
		super(value);
	}
	
	public SyncableDouble() {
		this(0.0d);
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = stream.readDouble();
	}

	@Override
	public void writeToStream(DataOutputStream stream) throws IOException {
		stream.writeDouble((Double)value);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		if (tiles.size() > 1) {
			tag.setDouble(name, (Double)value / tiles.size());
		}else {
			tag.setDouble(name, (Double)value);
		}
		super.writeToNBT(tag, name);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = tag.getDouble(name);
		}
		super.readFromNBT(tag, name);
	}

	@Override
	public void merge(ISyncableObject o) {
		if (o instanceof SyncableDouble) {
			modify((Double)((SyncableDouble)o).getValue());
			((SyncableDouble)o).setValue(0.0);
		}
	}

	@Override
	public void clear() {
		value = 0.0d;
	}

	public void modify(double by) {
		this.setValue((Double)this.getValue() + by);
	}
}
