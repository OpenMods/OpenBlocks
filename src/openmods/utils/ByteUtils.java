package openmods.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class ByteUtils {

	public static short set(short val, int slot, boolean bool) {
		if (get(val, slot) != bool) {
			val += bool? (1 << slot) : -(1 << slot);
		}
		return val;
	}

	public static short set(short val, Enum<?> slot, boolean bool) {
		return set(val, slot.ordinal(), bool);
	}

	public static boolean get(short val, int slot) {
		return (val & (1 << slot)) != 0;
	}

	public static boolean get(short val, Enum<?> slot) {
		return get(val, slot.ordinal());
	}

	public static void writeVLI(DataOutput output, int value) {
		// I'm not touching signed integers.
		Preconditions.checkArgument(value >= 0, "Value cannot be negative");

		try {
			while (true) {
				int b = value & 0x7F;
				int next = value >> 7;
				if (next > 0) {
					b |= 0x80;
					output.writeByte(b);
					value = next;
				} else {
					output.writeByte(b);
					break;
				}
			}
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}

	public static int readVLI(DataInput input) {
		int result = 0;
		int shift = 0;
		int b;
		try {
			do {
				b = input.readByte();
				result = result | ((b & 0x7F) << shift);
				shift += 7;
			} while (b < 0);
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		return result;
	}

	public static int nextPowerOf2(int v) {
		v--;
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		v++;

		return v;
	}

	public static boolean isPowerOfTwo(int size) {
		return (size & (size - 1)) == 0;
	}
}
