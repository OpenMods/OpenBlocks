package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockCannon extends OpenBlock {

	public BlockCannon() {
		super(Material.rock);
		setBlockBounds(0.3f, 0, 0.3f, 0.6f, 0.7f, 0.7f);
		setRenderMode(RenderMode.TESR_ONLY);
		/* I don't think this should rotate */
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return isOnTopOfSolidBlock(world, x, y, z, side);
	}
}
