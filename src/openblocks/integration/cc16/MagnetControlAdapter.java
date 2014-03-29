package openblocks.integration.cc16;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.entity.EntityMagnet;
import openblocks.integration.MagnetControlAdapterBase;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;

public class MagnetControlAdapter extends MagnetControlAdapterBase {

	public class Owner extends OwnerBase {

		@Override
		public boolean isValid(EntityMagnet magnet) {
			return turtle != null && turtle.getWorld() != null;
		}

		@Override
		public Vec3 getTarget() {
			return getTarget(getTurtlePosition(), getTurtleFacing());
		}
	}

	private final TurtleSide side;

	private final ITurtleAccess turtle;

	public MagnetControlAdapter(ITurtleAccess turtle, TurtleSide side) {
		this.turtle = turtle;
		this.side = side;
	}

	@Override
	public World getWorld() {
		return turtle.getWorld();
	}

	@Override
	protected OwnerBase createOwner() {
		return new Owner();
	}

	@Override
	protected boolean consumeFuel(int amount) {
		return turtle.consumeFuel(amount);
	}

	@Override
	protected SpawnSide getSpawnSide() {
		switch (side) {
			case Left:
				return SpawnSide.Left;
			case Right:
			default:
				return SpawnSide.Right;
		}
	}

	@Override
	protected ForgeDirection getTurtleFacing() {
		return ForgeDirection.getOrientation(turtle.getDirection());
	}

	@Override
	protected Vec3 getTurtlePosition() {
		ChunkCoordinates coord = turtle.getPosition();
		return Vec3.createVectorHelper(coord.posX, coord.posY, coord.posZ);
	}
}
