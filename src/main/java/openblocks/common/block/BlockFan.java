package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;

public class BlockFan extends OpenBlock {

	public BlockFan() {
		super(Config.blockFanId, Material.circuits);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 1.0f, 0.8f);
		/* No rotation, handled by TE */
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return isOnTopOfSolidBlock(world, x, y, z, side);
	}
}
