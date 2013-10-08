package openblocks.utils;

import net.minecraftforge.common.ForgeDirection;

public class Coord {
	public int x;
	public int y;
	public int z;

	public Coord() {}

	public Coord(ForgeDirection direction) {
		x = direction.offsetX;
		y = direction.offsetY;
		z = direction.offsetZ;
	}

	public Coord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void offset(ForgeDirection direction) {
		x += direction.offsetX;
		y += direction.offsetY;
		z += direction.offsetZ;
	}

	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public int hashCode() {
		return (x + 128) << 16 | (y + 128) << 8 | (z + 128);
	}

	@Override
	public boolean equals(Object that) {
		if (!(that instanceof Coord)) { return false; }
		Coord otherCoord = (Coord)that;
		return otherCoord.x == x && otherCoord.y == y && otherCoord.z == z;
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s", x, y, z);
	}

	public void offset(int ox, int oy, int oz) {
		x += ox;
		y += oy;
		z += oz;
	}

	public void setFrom(Coord copy) {
		x = copy.x;
		y = copy.y;
		z = copy.z;
	}

	@Override
	public Coord clone() {
		return new Coord(x, y, z);
	}

}
