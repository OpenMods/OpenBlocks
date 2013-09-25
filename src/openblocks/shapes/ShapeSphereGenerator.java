package openblocks.shapes;

import openblocks.utils.GeometryUtils;
import openblocks.utils.GeometryUtils.Octant;

public class ShapeSphereGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int radiusX, int radiusY, int radiusZ, IShapeable shapeable) {
		GeometryUtils.makeSphere(radiusX, radiusY, radiusZ, shapeable, Octant.ALL);
	}
}
