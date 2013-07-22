package jadedladder.shapes;

import jadedladder.JadedLadder;
import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;
import jadedladder.utils.GeometryUtils;
import jadedladder.utils.MathUtils;
import jadedladder.utils.GeometryUtils.Octant;

public class ShapeSphereGenerator implements IShapeGenerator {
	
	@Override
	public void generateShape(int radiusX, int radiusY, int radiusZ, IShapeable shapeable) {
		GeometryUtils.makeSphere(radiusX, radiusY, radiusZ, shapeable, Octant.ALL);
	}
}
