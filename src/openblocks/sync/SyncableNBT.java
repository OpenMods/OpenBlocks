package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

/***
 * Note: you must manually .markDirty() right now
 */
public class SyncableNBT extends SyncableObjectBase {

	private NBTTagCompound tag;
	
	public SyncableNBT() {
		tag = new NBTTagCompound();
	}
	
	public SyncableNBT(NBTTagCompound nbt) {
		tag = nbt;
	}
	
	public NBTTagCompound getTag() {
		return tag;
	}
	
	public void setTag(NBTTagCompound tag) {
		this.tag = tag;
	}
	
	@Override
	public void readFromStream(DataInput stream) throws IOException {
		short length = stream.readShort();
        if (length < 0) {
            tag = null;
        } else {
            byte[] abyte = new byte[length];
            stream.readFully(abyte);
            tag = CompressedStreamTools.decompress(abyte);
        }
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData) throws IOException {
		if (tag == null) {
			stream.writeShort(-1);
        } else {
            byte[] abyte = CompressedStreamTools.compress(tag);
            stream.writeShort((short)abyte.length);
            stream.write(abyte);
        }
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String name) {
		nbt.setCompoundTag(name, nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String name) {
		nbt.getCompoundTag(name);
	}

}
