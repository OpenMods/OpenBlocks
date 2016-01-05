package openblocks.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IPaintableBlock {

	/**
	 * 24-bit counterpart of Block.recolourBlock
	 */
	public boolean recolourBlockRGB(World world, int x, int y, int z, EnumFacing side, int colour);

}
