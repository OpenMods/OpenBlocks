package openblocks.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPaintableBlock {

	/**
	 * 24-bit counterpart of Block.recolorBlock
	 */
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, int colour);

}
