package jadedladder.shapes;

import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;

public class ShapeCuboidGenerator implements IShapeGenerator {

	private void makeFace (int startX, int startY, int startZ, int finishX, int finishY, int finishZ, IShapeable shapeable) {
		for (int x = startX; x <= finishX; x++) {
			for (int y = startY; y <= finishY; y++) {
				for (int z = startZ; z <= finishZ; z++) {
					shapeable.setBlock(x, y, z);
				}	
			}
		}
	}
	
	@Override
	public void generateShape(int xSize, int ySize, int zSize, IShapeable shapeable) {
		makeFace(-xSize,  ySize, -zSize,  xSize,  ySize,  zSize, shapeable); //top
		makeFace(-xSize, -ySize, -zSize,  xSize, -ySize,  zSize, shapeable); //bottom
		makeFace( xSize, -ySize, -zSize,  xSize,  ySize,  zSize, shapeable); //south
		makeFace(-xSize, -ySize, -zSize, -xSize,  ySize,  zSize, shapeable); //north
		makeFace(-xSize, -ySize, -zSize,  xSize,  ySize, -zSize, shapeable); //west
		makeFace(-xSize, -ySize,  zSize,  xSize,  ySize,  zSize, shapeable); //east
	}

}
