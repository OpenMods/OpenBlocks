package openblocks.common;

import com.google.common.base.Preconditions;
import java.math.BigInteger;

public enum StencilPattern implements IStencilPattern {

	CREEPER_FACE("                " +
			"                " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"      XXXX      " +
			"      XXXX      " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"    XX    XX    " +
			"    XX    XX    " +
			"                " +
			"                "),
	BORDER("XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              " +
			"XX              "),
	STRIPES("X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X " +
			"X X X X X X X X "),
	CORNER("                " +
			"                " +
			"  XXXXXX        " +
			"  XXXXXX        " +
			"  XX            " +
			"  XX            " +
			"  XX            " +
			"  XX            " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                "),
	CORNER2("XXXXXXXX        " +
			"XXXXXXX         " +
			"XXXXXX          " +
			"XXXXX           " +
			"XXXX            " +
			"XXX             " +
			"XX              " +
			"X               " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                "),
	CORNER3("                " +
			" XXXXXXX        " +
			" XXXXXXX        " +
			" XX             " +
			" XX XXXX        " +
			" XX XXXX        " +
			" XX XX          " +
			" XX XX          " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                " +
			"                "),
	HOLE("                " +
			"                " +
			"                " +
			"                " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"    XXXXXXXX    " +
			"                " +
			"                " +
			"                " +
			"                "),
	SPIRAL("                " +
			"XXXXXXXXXXXXXXX " +
			"              X " +
			" XXXXXXXXXXXX X " +
			" X          X X " +
			" X XXXXXXXX X X " +
			" X X      X X X " +
			" X X XXXX X X X " +
			" X X X  X X X X " +
			" X X X    X X X " +
			" X X XXXXXX X X " +
			" X X        X X " +
			" X XXXXXXXXXX X " +
			" X            X " +
			" XXXXXXXXXXXXXX " +
			"                "),
	THICKSTRIPES("  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  "),
	SPLAT(" XX     X    XX " +
			"XXX    XXX   XXX" +
			"XX     XX      X" +
			"    X      XX   " +
			"   XXX    XXX   " +
			"   XXX    X    X" +
			"    XX      XXXX" +
			"              XX" +
			"        X       " +
			"       XX  XX   " +
			" XX   XXX     X " +
			" XXX  XXX     XX" +
			" XXX   X        " +
			"           XX   " +
			"    XX     XXX  " +
			"    XXX     XX  "),
	STORAGE("                " +
			"                " +
			"                " +
			"   XXXXXXXXXX   " +
			"   X        X   " +
			"   X        X   " +
			"   X   XX   X   " +
			"   XXXXXXXXXX   " +
			"   X   XX   X   " +
			"   X        X   " +
			"   X        X   " +
			"   X        X   " +
			"   XXXXXXXXXX   " +
			"                " +
			"                " +
			"                "),
	HEART("                " +
			"                " +
			"   XXX    XXX   " +
			"  X   X  X   X  " +
			" X     XX     X " +
			" X            X " +
			" X            X " +
			" X            X " +
			"  X          X  " +
			"   X        X   " +
			"    X      X    " +
			"     X    X     " +
			"      X  X      " +
			"       XX       " +
			"                " +
			"                "),
	HEART2("                " +
			"                " +
			"                " +
			"   XXX    XXX   " +
			"  XXXXX  XXXXX  " +
			"  XXXXXXXXXXXX  " +
			"  XXXXXXXXXXXX  " +
			"  XXXXXXXXXXXX  " +
			"   XXXXXXXXXX   " +
			"    XXXXXXXX    " +
			"     XXXXXX     " +
			"      XXXX      " +
			"       XX       " +
			"                " +
			"                " +
			"                "),
	MUSIC("                " +
			"                " +
			"       XXXXXX   " +
			"  XXXXXXXXXXX   " +
			"  XXXXXX    X   " +
			"  X         X   " +
			"  X         X   " +
			"  X         X   " +
			"  X         X   " +
			"  X         XX  " +
			"  XX        XXX " +
			"  XXX       XXX " +
			"  XXX        X  " +
			"   X            " +
			"                " +
			"                "),
	BALLOON("                " +
			"      XXXX      " +
			"     XXXXXX     " +
			"    XXXXXXXX    " +
			"   XXXXXXXXXX   " +
			"   XXXXXXXXXX   " +
			"   XXXXXXXXXX   " +
			"   XXXXXXXXXX   " +
			"   XXXXXXXXXX   " +
			"    XXXXXXXX    " +
			"     XXXXXX     " +
			"      XXXX      " +
			"       X        " +
			"      XXX       " +
			"         X      " +
			"          XXX   ");

	private final BigInteger bits;

	private StencilPattern(String format) {
		BigInteger tmp = BigInteger.ZERO;
		Preconditions.checkState(format.length() == 16 * 16, "Invalid format string length");
		for (int i = 0; i < 256; i++)
			if (format.charAt(i) != ' ')
				tmp = tmp.setBit(i);

		bits = tmp;
	}

	@Override
	public int width() {
		return 16;
	}

	@Override
	public int height() {
		return 16;
	}

	@Override
	public int mix(int bitIndex, int src, int dst) {
		boolean bit = bits.testBit(bitIndex);
		return bit? src : dst;
	}

}
