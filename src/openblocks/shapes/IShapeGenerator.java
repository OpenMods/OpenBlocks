package openblocks.shapes;

public interface IShapeGenerator {

	/**
	 * Generates a shape and applies it to the shapeable object
	 * 
	 * @param xSize
	 *            Size along the x plane (Width)
	 * @param ySize
	 *            Size along the y plane (Height)
	 * @param zSize
	 *            Size along the z plane (Depth)
	 * @param shapable
	 *            Object that needs to be shaped
	 * @return the amount of blocks that were set
	 */
	public void generateShape(int xSize, int ySize, int zSize,
			IShapeable shapeable);

}
