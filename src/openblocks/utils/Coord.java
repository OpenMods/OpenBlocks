package openblocks.utils;

public class Coord {
	public int x;
	public int y;
	public int z;

	public Coord() {
		
	}
	
	public Coord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(int x, int y, int z) {
		this.x = x;
		this.y = x;
		this.z = z;
	}
	
	@Override
	public int hashCode() {
		return (x + 128) << 16 | (y + 128) << 8 | (z + 128);
	}

	@Override
	public boolean equals( Object that ) {
		if (!(that instanceof Coord)) {
			return false;
		}
		return ((Coord) that).hashCode() == hashCode();
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
	
	public Coord clone() {
		return new Coord(x, y, z);
	}
	
}
