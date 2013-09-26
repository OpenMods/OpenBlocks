package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class SyncableDirection implements ISyncableObject {

	private ForgeDirection value = ForgeDirection.UNKNOWN;
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

	public ForgeDirection getValue() {
		return value;
	}

	public void setValue(ForgeDirection direction) {
		if (direction != value) {
			setHasChanged();
			value = direction;
		}
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		value = ForgeDirection.getOrientation(stream.readByte());
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData) throws IOException {
		stream.writeByte(value.ordinal());
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			value = ForgeDirection.getOrientation(tag.getByte(name));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		tag.setByte(name, (byte)value.ordinal());
	}

}
