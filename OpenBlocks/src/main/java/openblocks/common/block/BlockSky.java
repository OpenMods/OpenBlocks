package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation(customName = "sky.normal")
public class BlockSky extends OpenBlock {
	private static final int MASK_POWERED = 1 << 1;

	private static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	// TODO sub-class to instance when registry changes are done
	public static class Inverted extends BlockSky {
		@Override
		protected boolean isInverted() {
			return true;
		}
	}

	protected boolean isInverted() {
		return false;
	}

	public BlockSky() {
		super(Material.IRON);
		setDefaultState(getDefaultState().withProperty(POWERED, false));
		setRequiresInitialization(true);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getPropertyOrientation(), POWERED);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(POWERED, (meta & MASK_POWERED) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(POWERED)? MASK_POWERED : 0;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block neighbour, BlockPos neigbourPos) {
		updatePowerState(state, world, blockPos);
		super.neighborChanged(state, world, blockPos, neighbour, neigbourPos);
	}

	@Override
	protected boolean onBlockAddedNextTick(World world, BlockPos blockPos, BlockState state) {
		updatePowerState(state, world, blockPos);
		return super.onBlockAddedNextTick(world, blockPos, state);
	}

	private void updatePowerState(BlockState state, World world, BlockPos pos) {
		if (!world.isRemote) {
			final boolean isPowered = world.isBlockIndirectlyGettingPowered(pos) > 0;
			final boolean isActive = state.getValue(POWERED);

			if (isPowered != isActive) world.scheduleUpdate(pos, this, 1);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random random) {
		final boolean isPowered = world.isBlockIndirectlyGettingPowered(pos) > 0;

		world.setBlockState(pos, state.withProperty(POWERED, isPowered));
	}

	public boolean isActive(BlockState state) {
		boolean isPowered = state.getValue(POWERED);
		return isPowered ^ isInverted();
	}

	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World world, BlockPos pos) {
		return isActive(state)? EMPTY : super.getSelectedBoundingBox(state, world, pos);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return isActive(state)? BlockRenderType.ENTITYBLOCK_ANIMATED : BlockRenderType.MODEL;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
}
