package openblocks.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IElevatorBlock {

	enum PlayerRotation {
		NONE, NORTH, SOUTH, EAST, WEST
	}

	EnumDyeColor getColor(World world, BlockPos pos, IBlockState state);

	PlayerRotation getRotation(World world, BlockPos pos, IBlockState state);
}
