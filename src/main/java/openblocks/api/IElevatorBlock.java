package openblocks.api;

import net.minecraft.world.World;

public interface IElevatorBlock {

	public enum PlayerRotation {
		NONE, NORTH, SOUTH, EAST, WEST;
	}

	public int getColor(World world, int x, int y, int z);

	public PlayerRotation getRotation(World world, int x, int y, int z);
}
