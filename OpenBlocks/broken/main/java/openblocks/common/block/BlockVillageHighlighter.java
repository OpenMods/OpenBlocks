package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockNotifyFlags;

@BookDocumentation
public class BlockVillageHighlighter extends OpenBlock.FourDirections {

	private static final int MASK_POWERED = 0x8;

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockVillageHighlighter() {
		super(Material.ROCK);
		setDefaultState(getDefaultState().withProperty(POWERED, false));
		setRequiresInitialization(true);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getPropertyOrientation(), POWERED);
	}

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
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, Direction side) {
		TileEntityVillageHighlighter tile = getTileEntity(blockAccess, pos, TileEntityVillageHighlighter.class);
		return (tile != null)? tile.getSignalStrength() : 0;
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, Direction side) {
		// TODO Side-aware?
		return getWeakPower(blockState, blockAccess, pos, side);
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
		if (!(world instanceof ServerWorld)) return;

		boolean isPowered = world.isBlockIndirectlyGettingPowered(blockPos) > 0;

		final BlockState newState = state.withProperty(POWERED, isPowered);
		if (state != newState)
			world.setBlockState(blockPos, newState, BlockNotifyFlags.ALL);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == Direction.DOWN? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.UNDEFINED;
	}
}
