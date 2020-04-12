package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockNotifyFlags;

@BookDocumentation
public class BlockXPShower extends OpenBlock.FourDirections {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(7.0 / 16.0, 7.0 / 16.0, 7.0 / 16.0, 9.0 / 16.0, 9.0 / 16.0, 16.0 / 16.0);

	public BlockXPShower() {
		super(Material.ROCK);
		setPlacementMode(BlockPlacementMode.SURFACE);
		setRequiresInitialization(true);
	}

	public static final IProperty<Boolean> POWERED = PropertyBool.create("powered");

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getPropertyOrientation(), POWERED);
	}

	private static final int MASK_POWERED = 0x8;

	@Override
	public BlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta)
				.withProperty(POWERED, (meta & MASK_POWERED) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return super.getMetaFromState(state) | (state.getValue(POWERED)? MASK_POWERED : 0);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		Orientation orientation = getOrientation(source, pos);
		return BlockSpaceTransform.instance.mapBlockToWorld(orientation, AABB);
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, Direction side) {
		switch (side) {
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				return true;
			default:
				return false;
		}
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block neighbour, BlockPos neigbourPos) {
		updateRedstone(world, blockPos, state);
		super.neighborChanged(state, world, blockPos, neighbour, neigbourPos);
	}

	@Override
	protected boolean onBlockAddedNextTick(World world, BlockPos blockPos, BlockState state) {
		updateRedstone(world, blockPos, state);
		return super.onBlockAddedNextTick(world, blockPos, state);
	}

	private static void updateRedstone(World world, BlockPos blockPos, BlockState state) {
		if (world.isRemote) return;
		boolean isPowered = world.isBlockIndirectlyGettingPowered(blockPos) > 0;
		final BlockState newState = state.withProperty(POWERED, isPowered);
		if (state != newState) world.setBlockState(blockPos, newState, BlockNotifyFlags.ALL);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
}
