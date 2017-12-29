package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation(customName = "sky.normal")
public class BlockSky extends OpenBlock {

	private static final int MASK_INVERTED = 1 << 0;
	private static final int MASK_POWERED = 1 << 1;

	private static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	public static final PropertyBool INVERTED = PropertyBool.create("inverted");

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public static boolean isInverted(int meta) {
		return (meta & MASK_INVERTED) != 0;
	}

	public BlockSky() {
		super(Material.IRON);
		setDefaultState(getDefaultState().withProperty(POWERED, false));
		setRequiresInitialization(true);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getPropertyOrientation(), INVERTED, POWERED);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(POWERED, (meta & MASK_POWERED) != 0)
				.withProperty(INVERTED, (meta & MASK_INVERTED) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		final int isPowered = state.getValue(POWERED)? MASK_POWERED : 0;
		final int isInverted = state.getValue(INVERTED)? MASK_INVERTED : 0;

		return isPowered | isInverted;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos blockPos, Block neighbour, BlockPos neigbourPos) {
		updatePowerState(state, world, blockPos);
		super.neighborChanged(state, world, blockPos, neighbour, neigbourPos);
	}

	@Override
	protected boolean onBlockAddedNextTick(World world, BlockPos blockPos, IBlockState state) {
		updatePowerState(state, world, blockPos);
		return super.onBlockAddedNextTick(world, blockPos, state);
	}

	private void updatePowerState(IBlockState state, World world, BlockPos pos) {
		if (!world.isRemote) {
			final boolean isPowered = world.isBlockIndirectlyGettingPowered(pos) > 0;
			final boolean isActive = state.getValue(POWERED);

			if (isPowered != isActive) world.scheduleUpdate(pos, this, 1);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		final boolean isPowered = world.isBlockIndirectlyGettingPowered(pos) > 0;

		world.setBlockState(pos, state.withProperty(POWERED, isPowered));
	}

	public static boolean isActive(IBlockState state) {
		boolean isPowered = state.getValue(POWERED);
		boolean isInverted = state.getValue(INVERTED);
		return isPowered ^ isInverted;
	}

	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		return isActive(state)? EMPTY : super.getSelectedBoundingBox(state, world, pos);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return isActive(state)? EnumBlockRenderType.ENTITYBLOCK_ANIMATED : EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
}
