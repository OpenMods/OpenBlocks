package openblocks.utils;

public class ByteUtils {

	public static short set(short val, int slot, boolean bool) {
		if (get(val, slot) != bool) {
			val += bool? (1 << slot) : -(1 << slot);
		}
		return val;
	}

	public static short set(short val, Enum slot, boolean bool) {
		return set(val, slot.ordinal(), bool);
	}

	public static boolean get(short val, int slot) {
		return (val & (1 << slot)) != 0;
	}
	
	public static boolean get(short val, Enum slot) {
		return get(val, slot.ordinal());
	}
}
