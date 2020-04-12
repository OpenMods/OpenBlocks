package openblocks.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;

public enum StencilPattern implements IStencilPattern {

	// @formatter:off
	CREEPER_FACE(OpenBlocks.location("creeper_face"),
			"                " +
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
	BORDER(OpenBlocks.location("border"),
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
			"XX              " +
			"XX              "),
	STRIPES(OpenBlocks.location("stripes"),
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
			"X X X X X X X X " +
			"X X X X X X X X "),
	CORNER(OpenBlocks.location("corner"),
			"                " +
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
	CORNER2(OpenBlocks.location("corner2"),
			"XXXXXXXX        " +
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
	CORNER3(OpenBlocks.location("corner3"),
			"                " +
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
	HOLE(OpenBlocks.location("hole"),
			"                " +
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
	SPIRAL(OpenBlocks.location("spiral"),
			"                " +
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
	THICKSTRIPES(OpenBlocks.location("thick_stripes"),
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
			"  XXXX    XXXX  " +
			"  XXXX    XXXX  "),
	SPLAT(OpenBlocks.location("splat"),
			" XX     X    XX " +
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
	STORAGE(OpenBlocks.location("storage"),
			"                " +
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
	HEART(OpenBlocks.location("heart"),
			"                " +
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
	HEART2(OpenBlocks.location("heart2"),
			"                " +
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
	MUSIC(OpenBlocks.location("note"),
			"                " +
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
	BALLOON(OpenBlocks.location("balloon"),
			"                " +
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
	// @formatter:on

	public static final Map<ResourceLocation, StencilPattern> ID_TO_PATTERN = Stream.of(values()).collect(ImmutableMap.toImmutableMap(p -> p.id, Function.identity()));

	public final ResourceLocation id;

	private final BigInteger bits;

	StencilPattern(final ResourceLocation id, final String format) {
		this.id = id;
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
