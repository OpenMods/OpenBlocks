package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class SyncableString extends SyncableObjectBase {

	private String value;

	public void setValue(String val) {
		if (val != value) {
			value = val;
			markDirty();
		}
	}

	public String getValue() {
		return value;
	}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		value = stream.readUTF();
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData)
			throws IOException {
		stream.writeUTF(value);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String name) {
		nbt.setString(name, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String name) {
		value = nbt.getString(name);
	}

	public void clear() {
		setValue("");
	}

}
