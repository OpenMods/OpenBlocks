package openblocks.shapes;

import openmods.utils.MathUtils;

public class ShapeCylinderGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int radiusX, int height, int radiusZ, IShapeable shapeable) {
		if (height == 0) { return; }

		final double invRadiusX = 1.0 / radiusX;
		final double invRadiusZ = 1.0 / radiusZ;

		double nextXn = 0;
		forX: for (int x = 0; x <= radiusX; ++x) {
			final double xn = nextXn;
			nextXn = (x + 1) * invRadiusX;
			double nextZn = 0;
			forZ: for (int z = 0; z <= radiusZ; ++z) {
				final double zn = nextZn;
				nextZn = (z + 1) * invRadiusZ;

				double distanceSq = MathUtils.lengthSq(xn, zn);
				if (distanceSq > 1) {
					if (z == 0) {
						break forX;
					}
					break forZ;
				}

				if (MathUtils.lengthSq(nextXn, zn) <= 1
						&& MathUtils.lengthSq(xn, nextZn) <= 1) {
					continue;
				}

				for (int y = -height; y <= height; ++y) {
					shapeable.setBlock(x, y, z);
					shapeable.setBlock(-x, y, z);
					shapeable.setBlock(x, y, -z);
					shapeable.setBlock(-x, y, -z);
				}
			}
		}

	}

}
