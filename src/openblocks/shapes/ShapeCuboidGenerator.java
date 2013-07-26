package openblocks.shapes;

import net.minecraftforge.common.ForgeDirection;
import openblocks.utils.GeometryUtils;

public class ShapeCuboidGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int xSize, int ySize, int zSize,
			IShapeable shapeable) {

		/*
		 * The up direction is up when doing a side plane, and the origin is of
		 * course bottom left However this is not the case when doing the top
		 * and bottom, as their up direction is depth
		 */

		/*
		 * Optimization: The front and back planes are full sized, left and
		 * right are 2 blocks smaller so that they don't overlap the front and
		 * back planes. Top and bottom planes are 2 blocks smaller in both
		 * directions so they don't overlap any sides. This works for most cases
		 * unless two directions are the same because of rounding in small
		 * numbers.
		 * 
		 * But this should be fairly optimal without adding anything inefficient
		 * to complicate matters. - NeverCast
		 */

		/* Used for shrinking some planes to prevent iterating the same block */
		/*
		 * Basically, the size is size * 2 - 2 unless that's less than 1 and
		 * size is > 0
		 */
		int xSizeAdj = xSize == 1 ? 1 : xSize * 2 - 2;
		int zSizeAdj = zSize == 1 ? 1 : zSize * 2 - 2;
		// front (north)
		GeometryUtils.makePlane(-xSize, -ySize, -zSize, xSize * 2, ySize * 2,
				ForgeDirection.EAST, ForgeDirection.UP, shapeable);
		// back ( south )
		GeometryUtils.makePlane(xSize, -ySize, zSize, xSize * 2, ySize * 2,
				ForgeDirection.WEST, ForgeDirection.UP, shapeable);
		// left ( west )
		GeometryUtils.makePlane(-xSize, -ySize, zSize - 1, zSizeAdj, ySize * 2,
				ForgeDirection.NORTH, ForgeDirection.UP, shapeable);
		// right ( east )
		GeometryUtils.makePlane(xSize, -ySize, -zSize + 1, zSizeAdj, ySize * 2,
				ForgeDirection.SOUTH, ForgeDirection.UP, shapeable);
		// top ( up )
		GeometryUtils.makePlane(-xSize + 1, ySize, -zSize + 1, xSizeAdj,
				zSizeAdj, ForgeDirection.EAST, ForgeDirection.SOUTH, shapeable);
		// bottom ( down )
		// Notice the 'Normal' of this plane is up, not down. If you were
		// wondering why it's the same as the top plane
		// Normally if you were rendering a cube in GL you would have to flip it
		// so the normal would be away from the center
		// But that doesn't matter in this case.
		GeometryUtils.makePlane(-xSize + 1, -ySize, -zSize + 1, xSizeAdj,
				zSizeAdj, ForgeDirection.EAST, ForgeDirection.SOUTH, shapeable);

	}

}
