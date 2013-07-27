package openblocks.utils;

public class ByteUtils {

	public static short set(short val, int slot, boolean bool) {
		if (get(val, slot) != bool) {
			val += bool ? (1 << slot) : -(1 << slot);
		}
		return val;
	}

	public static boolean get(short val, int slot) {
		return (val & (1 << slot)) != 0;
	}
}
