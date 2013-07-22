package jadedladder.utils;

import jadedladder.common.IShapeable;
import net.minecraftforge.common.ForgeDirection;

public class GeometryUtils {
	public static void makeLine(int startX, int startY, int startZ, ForgeDirection direction, int length, IShapeable shapeable){
		if(length == 0) return;
		for(int offset = 0; offset <= length; offset++) /* Create a line in the direction of direction, length in size */
			shapeable.setBlock(startX + (offset * direction.offsetX), startY + (offset * direction.offsetY), startZ + (offset * direction.offsetZ));
	}
	
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
