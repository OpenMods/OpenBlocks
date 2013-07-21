package jadedladder.shapes;

import jadedladder.JadedLadder;
import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;
import jadedladder.utils.MathUtils;

public class ShapeSphereGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int radiusX, int radiusY, int radiusZ,
			IShapeable shapeable) {

		
        final double invRadiusX = 1.0 / radiusX;
        final double invRadiusY = 1.0 / radiusY;
        final double invRadiusZ = 1.0 / radiusZ;
       
        double nextXn = 0;
        forX: for (int x = 0; x <= radiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0;
            forY: for (int y = 0; y <= radiusY; ++y) {
                final double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0;
                forZ: for (int z = 0; z <= radiusZ; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = MathUtils.lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }

                    if (MathUtils.lengthSq(nextXn, yn, zn) <= 1 && MathUtils.lengthSq(xn, nextYn, zn) <= 1 && MathUtils.lengthSq(xn, yn, nextZn) <= 1) {
                        continue;
                    }
                    
                    shapeable.setBlock(x, y, z);
                    shapeable.setBlock(-x, y, z);
                    shapeable.setBlock(x, -y, z);
                    shapeable.setBlock(x, y, -z);
                    shapeable.setBlock(-x, -y, z);
                    shapeable.setBlock(x, -y, -z);
                    shapeable.setBlock(-x, y, -z);
                    shapeable.setBlock(-x, -y, -z);
                }
            }
        }

	}

}
