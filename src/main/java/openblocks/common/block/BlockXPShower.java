package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
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
		return new BlockStateContainer(this, new IProperty[] { getPropertyOrientation(), POWERED });
	}

	private static final int MASK_POWERED = 0x8;

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta)
				.withProperty(POWERED, (meta & MASK_POWERED) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) | (state.getValue(POWERED)? MASK_POWERED : 0);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		Orientation orientation = getOrientation(source, pos);
		return BlockSpaceTransform.instance.mapBlockToWorld(orientation, AABB);
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
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
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
	public void neighborChanged(IBlockState state, World world, BlockPos blockPos, Block neighbour, BlockPos neigbourPos) {
		updateRedstone(world, blockPos, state);
		super.neighborChanged(state, world, blockPos, neighbour, neigbourPos);
	}

	@Override
	protected boolean onBlockAddedNextTick(World world, BlockPos blockPos, IBlockState state) {
		updateRedstone(world, blockPos, state);
		return super.onBlockAddedNextTick(world, blockPos, state);
	}

	private static void updateRedstone(World world, BlockPos blockPos, IBlockState state) {
		if (world.isRemote) return;
		boolean isPowered = world.isBlockIndirectlyGettingPowered(blockPos) > 0;
		final IBlockState newState = state.withProperty(POWERED, isPowered);
		if (state != newState) world.setBlockState(blockPos, newState, BlockNotifyFlags.ALL);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
}
