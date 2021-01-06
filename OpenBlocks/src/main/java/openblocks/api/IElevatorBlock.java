package openblocks.api;

import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IElevatorBlock {
	enum PlayerRotation {
		NONE, NORTH, SOUTH, EAST, WEST
	}

	DyeColor getColor(World world, BlockPos pos, BlockState state);

	PlayerRotation getRotation(World world, BlockPos pos, BlockState state);
}
