package jadedladder.shapes;

import jadedladder.JadedLadder;
import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;

public class ShapeCylinderGenerator implements IShapeGenerator {

	@Override
	public int generateShape(double radiusX, double height, double radiusZ,
			IShapeable shapeable) {
		int affected = 0;

		radiusX += 0.5;
		radiusZ += 0.5;

		if (height == 0) {
			return 0;
		}

		final double invRadiusX = 1 / radiusX;
		final double invRadiusZ = 1 / radiusZ;

		final int ceilRadiusX = (int) Math.ceil(radiusX);
		final int ceilRadiusZ = (int) Math.ceil(radiusZ);

		double nextXn = 0;
		forX: for (int x = 0; x <= ceilRadiusX; ++x) {
			final double xn = nextXn;
			nextXn = (x + 1) * invRadiusX;
			double nextZn = 0;
			forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
				final double zn = nextZn;
				nextZn = (z + 1) * invRadiusZ;

				double distanceSq = JadedLadder.proxy.lengthSq(xn, zn);
				if (distanceSq > 1) {
					if (z == 0) {
						break forX;
					}
					break forZ;
				}

				if (JadedLadder.proxy.lengthSq(nextXn, zn) <= 1
						&& JadedLadder.proxy.lengthSq(xn, nextZn) <= 1) {
					continue;
				}

				for (int y = 0; y < height; ++y) {
					if (shapeable.setBlock(x, y, z)) {
						++affected;
					}
					if (shapeable.setBlock(-x, y, z)) {
						++affected;
					}
					if (shapeable.setBlock(x, y, -z)) {
						++affected;
					}
					if (shapeable.setBlock(-x, y, -z)) {
						++affected;
					}
				}
			}
		}

		return affected;
	}

}
