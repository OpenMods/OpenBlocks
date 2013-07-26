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

	@Override
	public int hashCode() {
		return x + 128 << 16 | y + 128 << 8 | z + 128;
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof Coord
				&& ((Coord) o).hashCode() == hashCode();
	}
}
