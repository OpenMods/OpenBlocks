package openblocks.utils;

public class Coord {
	public int x;
	public int y;
	public int z;

	public Coord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getHash() {
		return getHash(x, y, z);
	}

	public static int getHash(int x, int y, int z) {
		return x + 128 << 16 | y + 128 << 8 | z + 128;
	}
}
