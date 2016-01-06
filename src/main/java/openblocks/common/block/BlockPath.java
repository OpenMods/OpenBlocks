package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockPath extends OpenBlock {

	public BlockPath() {
		super(Material.ground);
		setBlockBounds(0, 0, 0, 1f, 0.1f, 1f);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos blockPos, IBlockState state) {
		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return isNeighborBlockSolid(world, pos, EnumFacing.DOWN) && super.canPlaceBlockAt(world, pos);
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		if (!world.isRemote && !isNeighborBlockSolid(world, pos, EnumFacing.DOWN)) {
			world.destroyBlock(pos, true);
		}
	}

}
