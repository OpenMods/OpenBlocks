package openblocks.api;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IPaintableBlock {

	/**
	 * 24-bit counterpart of Block.recolourBlock
	 */
	public boolean recolourBlockRGB(World world, int x, int y, int z, ForgeDirection side, int colour);

}
