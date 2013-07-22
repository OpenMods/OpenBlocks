package jadedladder.shapes;

import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;
import jadedladder.utils.GeometryUtils;
import jadedladder.utils.GeometryUtils.Octant;

public class ShapeDomeGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int xSize, int ySize, int zSize,
			IShapeable shapeable) {
		GeometryUtils.makeSphere(xSize, ySize, zSize, shapeable, Octant.TOP);
	}

}
