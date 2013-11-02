package openblocks.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

// NOTE: I'm aware of java.util.BitSet, but it has no byte[] convenrsion functions in Java < 7, so here is derpy version
public class BitSet {
	private byte[] bits;

	public BitSet() {
		bits = new byte[0];
	}

	public BitSet(int bitCount) {
		resize(bitCount);
	}

	public void resize(int bitCount) {
		int count = (bitCount + 7) >> 3;
		bits = new byte[count];
	}

	public void setBit(int bit) {
		int field = bit >> 3;
		int pos = bit & 7;
		bits[field] |= (1 << pos);
	}

	public void clearBit(int bit) {
		int field = bit >> 3;
		int pos = bit & 7;
		bits[field] &= ~(1 << pos);
	}

	public boolean testBit(int bit) {
		int field = bit >> 3;
		int pos = bit & 7;
		return (bits[field] & (1 << pos)) != 0;
	}

	public void writeToStream(DataOutput output) throws IOException {
		output.write(bits);
	}

	public void readFromStream(DataInput input) throws IOException {
		input.readFully(bits);
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setByteArray("Bits", bits);
	}

	public void readFromNBT(NBTTagCompound tag) {
		bits = tag.getByteArray("Bits").clone();
	}
}
