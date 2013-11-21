package openmods.utils;

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

	private static int byteCount(int bitCount) {
		return (bitCount + 7) >> 3;
	}

	public void resize(int bitCount) {
		int count = byteCount(bitCount);
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
		ByteUtils.writeVLI(output, bits.length);
		output.write(bits);
	}

	public void readFromStream(DataInput input) throws IOException {
		int size = ByteUtils.readVLI(input);
		bits = new byte[size];
		input.readFully(bits);
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setByteArray("Bits", bits);
	}

	public void readFromNBT(NBTTagCompound tag) {
		bits = tag.getByteArray("Bits").clone();
	}

	public boolean checkSize(int bitSize) {
		return byteCount(bitSize) <= bits.length;
	}
}
