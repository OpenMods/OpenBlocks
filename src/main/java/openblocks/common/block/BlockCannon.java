package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openmods.block.OpenBlock;

public class BlockCannon extends OpenBlock {

	public BlockCannon() {
		super(Material.rock);
		setBlockBounds(0.3f, 0, 0.3f, 0.6f, 0.7f, 0.7f);
	}

	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		return null;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return isOnTopOfSolidBlock(world, pos, side);
	}
}
