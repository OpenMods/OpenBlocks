package jadedladder.shapes;

import net.minecraftforge.common.ForgeDirection;
import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;
import jadedladder.utils.GeometryUtils;

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
		
		/* 
		 * The up direction is up when doing a side plane, and the origin is of course bottom left
		 * However this is not the case when doing the top and bottom, as their up direction is depth
		 */

		// front (north)
		GeometryUtils.makePlane(-xSize, -ySize, -zSize, xSize * 2, ySize * 2, ForgeDirection.EAST, ForgeDirection.UP, shapeable); 
		// back ( south )
		GeometryUtils.makePlane(xSize, -ySize, zSize, xSize * 2, ySize * 2, ForgeDirection.WEST, ForgeDirection.UP, shapeable); 
		// left ( west )
		GeometryUtils.makePlane(-xSize, -ySize, zSize, zSize * 2, ySize * 2, ForgeDirection.NORTH, ForgeDirection.UP, shapeable);
		// right ( east )
		GeometryUtils.makePlane(xSize, -ySize, -zSize, zSize * 2, ySize * 2, ForgeDirection.SOUTH, ForgeDirection.UP, shapeable);
		// top ( up )
		GeometryUtils.makePlane(-xSize, ySize, -zSize, xSize * 2, zSize * 2, ForgeDirection.EAST, ForgeDirection.SOUTH, shapeable);
		// bottom ( down ) 
		// Notice the 'Normal' of this plane is up, not down. If you were wondering why it's the same as the top plane
		// Normally if you were rendering a cube in GL you would have to flip it so the normal would be away from the center
		// But that doesn't matter in this case.
		GeometryUtils.makePlane(-xSize, -ySize, -zSize, xSize * 2, zSize * 2, ForgeDirection.EAST, ForgeDirection.SOUTH, shapeable);
		
	}

}
