package openblocks.shapes;

import openblocks.utils.GeometryUtils;
import openblocks.utils.GeometryUtils.Octant;

public class ShapeDomeGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int xSize, int ySize, int zSize, IShapeable shapeable) {
		GeometryUtils.makeSphere(xSize, ySize, zSize, shapeable, Octant.TOP);
	}

}
