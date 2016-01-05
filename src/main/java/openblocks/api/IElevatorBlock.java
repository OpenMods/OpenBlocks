package openblocks.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IElevatorBlock {

	public enum PlayerRotation {
		NONE, NORTH, SOUTH, EAST, WEST;
	}

	public int getColor(World world, BlockPos pos, IBlockState state);

	public PlayerRotation getRotation(World world, BlockPos pos, IBlockState state);
}
