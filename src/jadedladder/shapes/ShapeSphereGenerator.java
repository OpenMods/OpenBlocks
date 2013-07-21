package jadedladder.shapes;

import jadedladder.JadedLadder;
import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;

public class ShapeSphereGenerator implements IShapeGenerator {

	@Override
	public int generateShape(double radiusX, double radiusY, double radiusZ,
			IShapeable shapeable) {
		int affected = 0;

        radiusX += 0.5;
        radiusY += 0.5;
        radiusZ += 0.5;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusY = 1 / radiusY;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusY = (int) Math.ceil(radiusY);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextXn = 0;
        forX: for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0;
            forY: for (int y = -ceilRadiusY; y <= ceilRadiusY; ++y) {
                final double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0;
                forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = JadedLadder.proxy.lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }

                    if (JadedLadder.proxy.lengthSq(nextXn, yn, zn) <= 1 && JadedLadder.proxy.lengthSq(xn, nextYn, zn) <= 1 && JadedLadder.proxy.lengthSq(xn, yn, nextZn) <= 1) {
                        continue;
                    }

                    if (shapeable.setBlock(x, y, z)) {
                        ++affected;
                    }
                    if (shapeable.setBlock(-x, y, z)) {
                        ++affected;
                    }
                    if (shapeable.setBlock(x, -y, z)) {
                        ++affected;
                    }
                    if (shapeable.setBlock(x, y, -z)) {
                        ++affected;
                    }
                    if (shapeable.setBlock(-x, -y, z)) {
                        ++affected;
                    }
                    if (shapeable.setBlock(x, -y, -z)) {
                        ++affected;
                    }
                    if (shapeable.setBlock(-x, y, -z)) {
                        ++affected;
                    }
                    if (shapeable.setBlock(-x, -y, -z)) {
                        ++affected;
                    }
                    
                }
            }
        }

        return affected;
	}

}
