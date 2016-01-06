package openblocks.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IElevatorBlock {

	public enum PlayerRotation {
		NONE, NORTH, SOUTH, EAST, WEST;
	}

	public EnumDyeColor getColor(World world, BlockPos pos, IBlockState state);

	public PlayerRotation getRotation(World world, BlockPos pos, IBlockState state);
}
