package openblocks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmods.block.OpenBlock;

public class BlockCannon extends OpenBlock {

	public BlockCannon() {
		super(Material.ROCK);
	}

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.3, 0.0, 0.3, 0.6, 0.7, 0.7);

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, Direction side) {
		return isOnTopOfSolidBlock(world, pos, side);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == Direction.DOWN? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.UNDEFINED;
	}
}
