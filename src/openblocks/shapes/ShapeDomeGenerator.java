package openblocks.shapes;

import openmods.utils.GeometryUtils;
import openmods.utils.GeometryUtils.Octant;

public class ShapeDomeGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int xSize, int ySize, int zSize, IShapeable shapeable) {
		GeometryUtils.makeSphere(xSize, ySize, zSize, shapeable, Octant.TOP);
	}

}
