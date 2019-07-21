package openblocks.client.renderer.block.canvas;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import net.minecraft.util.Direction;

public class StencilTextureProjection {

	private final Direction side;

	public StencilTextureProjection(Direction side) {
		this.side = side;
	}

	public Vector2f project(Vector3f position) {
		switch (side) {
			case NORTH:
				return new Vector2f(1 - position.x, 1 - position.y);
			case SOUTH:
				return new Vector2f(position.x, 1 - position.y);
			case EAST:
				return new Vector2f(1 - position.z, 1 - position.y);
			case WEST:
				return new Vector2f(position.z, 1 - position.y);
			case UP:
				return new Vector2f(position.x, position.z);
			case DOWN:
				return new Vector2f(position.x, 1 - position.z);
			default:
				throw new AssertionError(side);
		}
	}

}
