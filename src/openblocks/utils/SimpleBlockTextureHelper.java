package openblocks.utils;

import java.util.HashMap;

import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

public class SimpleBlockTextureHelper {

	private Icon side_up;
	private Icon side_right;
	private Icon side_down;
	private Icon side_left;
	private Icon top;
	private Icon bottom;

	private HashMap<ForgeDirection, Icon[]> orientations;
	
	public Icon setTop(Icon icon) {
		return top = icon;
	}

	public Icon setBottom(Icon icon) {
		return bottom = icon;
	}

	public Icon setSideLeft(Icon icon) {
		return side_left = icon;
	}

	public Icon setSideUp(Icon icon) {
		return side_up = icon;
	}

	public Icon setSideRight(Icon icon) {
		return side_right = icon;
	}

	public Icon setSideDown(Icon icon) {
		return side_down = icon;
	}

	private void setup() {
		orientations = new HashMap<ForgeDirection, Icon[]>();
		orientations.put(ForgeDirection.DOWN, new Icon[] { top, bottom, side_down, side_down, side_down, side_down });
		orientations.put(ForgeDirection.UP, new Icon[] { bottom, top, side_up, side_up, side_up, side_up });
		orientations.put(ForgeDirection.WEST, new Icon[] { side_left, side_left, side_right, side_left, top, bottom });
		orientations.put(ForgeDirection.EAST, new Icon[] { side_right, side_right, side_left, side_right, bottom, top });
		orientations.put(ForgeDirection.SOUTH, new Icon[] { side_down, side_down, bottom, top, side_right, side_left });
		orientations.put(ForgeDirection.NORTH, new Icon[] { side_up, side_up, top, bottom, side_left, side_right });
	}
	
	public Icon getIconForDirection(ForgeDirection direction, ForgeDirection side) {
		if (orientations == null) {
			setup();			
		}
		return orientations.get(direction)[side.ordinal()];
	}

}
