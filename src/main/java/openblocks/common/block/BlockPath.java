package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockPath extends OpenBlock {

	public BlockPath() {
		super(Material.GROUND);
	}

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1, 1.0);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canSpawnInBlock() {
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return isNeighborBlockSolid(world, pos, EnumFacing.DOWN) && super.canPlaceBlockAt(world, pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbour) {
		if (!world.isRemote && !isNeighborBlockSolid(world, pos, EnumFacing.DOWN)) {
			world.destroyBlock(pos, true);
		}
	}

}
