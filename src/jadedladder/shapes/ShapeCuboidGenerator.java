package jadedladder.shapes;

import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;

public class ShapeCuboidGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int xSize, int ySize, int zSize,
			IShapeable shapeable) {
		for (int x = -xSize; x <= xSize; x++) {
			for (int y = -ySize; y <= ySize; y++) {
				for (int z = -zSize; z <= zSize; z++) {
					if (y == -ySize || y == ySize || x == -xSize || x == xSize || z == -zSize || z == zSize) {
						shapeable.setBlock(x, y, z);
					}
				}	
			}
		}

	}

}
