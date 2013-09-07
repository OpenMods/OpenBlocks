package openblocks.api;

import net.minecraft.util.ChunkCoordinates;

public interface IShapeProvider {

	/**
	 * Gets a list of the coordinates for the current shape. All coordinates are
	 * absolute coordinates.
	 *
	 * @return An array of coordinates
	 */
	public ChunkCoordinates[] getShapeCoordinates();

}
