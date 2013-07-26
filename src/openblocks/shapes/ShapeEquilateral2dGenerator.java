package openblocks.shapes;

import openblocks.common.IShapeGenerator;
import openblocks.common.IShapeable;
import openblocks.utils.GeometryUtils;

public class ShapeEquilateral2dGenerator implements IShapeGenerator {

	private int sides;

	public ShapeEquilateral2dGenerator(int sides) {
		this.sides = sides;
	}

	@Override
	public void generateShape(int xSize, int ySize, int zSize,
			IShapeable shapeable) {
		int firstX = 0;
		int firstZ = 0;
		int previousX = 0;
		int previousZ = 0;

		for (int i = 0; i < sides; i++) {
			double d = 2 * Math.PI * i / sides;
			int x = (int) Math.round(Math.cos(d) * xSize);
			int z = (int) Math.round(Math.sin(d) * xSize);
			if (i == 0) {
				firstX = previousX = x;
				firstZ = previousZ = z;
			} else {
				GeometryUtils.line2D(0, previousX, previousZ, x, z, shapeable);
				previousX = x;
				previousZ = z;
			}
		}
		GeometryUtils
				.line2D(0, previousX, previousZ, firstX, firstZ, shapeable);
	}

}
