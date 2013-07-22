package jadedladder.utils;

import jadedladder.common.IShapeable;
import net.minecraftforge.common.ForgeDirection;

public class GeometryUtils {
	
	/**
	 * Makes a link of blocks in a shape
	 * @param startX X position of origin
	 * @param startY Y position of origin
	 * @param startZ Z position of origin
	 * @param direction Direction to apply the line
	 * @param length Length of the line
	 * @param shapeable Shapeable object to set the blocks
	 */
	public static void makeLine(int startX, int startY, int startZ, ForgeDirection direction, int length, IShapeable shapeable){
		if(length == 0) return;
		for(int offset = 0; offset <= length; offset++) /* Create a line in the direction of direction, length in size */
			shapeable.setBlock(startX + (offset * direction.offsetX), startY + (offset * direction.offsetY), startZ + (offset * direction.offsetZ));
	}
	
	/**
	 * Makes a flat plane along two directions
	 * @param startX X position of origin
	 * @param startY Y position of origin
	 * @param startZ Z position of origin
	 * @param width Width of the plane
	 * @param height Height of the plane
	 * @param right The right(width) direction of the plane
	 * @param up The up(height) direction of the plane
	 * @param shapeable The shape to apply the blocks to
	 */
	public static void makePlane(int startX, int startY, int startZ, int width, int height, ForgeDirection right, ForgeDirection up, IShapeable shapeable){
		if(width == 0 || height == 0) return;
		int lineOffsetX, lineOffsetY, lineOffsetZ; 
		// We offset each line by up, and then apply it right
		for(int h = 0; h <= height; h++){
			lineOffsetX = startX + (h * up.offsetX);
			lineOffsetY = startY + (h * up.offsetY);
			lineOffsetZ = startZ + (h * up.offsetZ);
			makeLine(lineOffsetX, lineOffsetY, lineOffsetZ, right, width, shapeable);
		}
	}
}
