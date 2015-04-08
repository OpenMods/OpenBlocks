package openblocks.client.renderer.tileentity.tank;

public interface INeighbourMap {
	public static final int DIR_NORTH = 1;
	public static final int DIR_SOUTH = 2;

	public static final int DIR_WEST = 4;
	public static final int DIR_EAST = 8;

	public static final int DIR_UP = 16;
	public static final int DIR_DOWN = 32;

	public static final INeighbourMap NO_NEIGHBOURS = new INeighbourMap() {
		@Override
		public boolean hasDirectNeighbour(int dir) {
			return false;
		}

		@Override
		public boolean hasDiagonalNeighbour(int direction1, int direction2) {
			return false;
		}
	};

	public boolean hasDirectNeighbour(int direction);

	public boolean hasDiagonalNeighbour(int direction1, int direction2);
}