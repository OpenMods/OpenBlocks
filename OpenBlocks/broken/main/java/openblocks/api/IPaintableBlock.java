package openblocks.api;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPaintableBlock {

	/**
	 * 24-bit counterpart of Block.recolorBlock
	 */
	boolean recolorBlock(World world, BlockPos pos, Direction side, int colour);

}
