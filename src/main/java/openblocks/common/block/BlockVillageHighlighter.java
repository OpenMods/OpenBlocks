package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta)
				.withProperty(POWERED, (meta & MASK_POWERED) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) | (state.getValue(POWERED)? MASK_POWERED : 0);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		TileEntityVillageHighlighter tile = getTileEntity(blockAccess, pos, TileEntityVillageHighlighter.class);
		return (tile != null)? tile.getSignalStrength() : 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		// TODO Side-aware?
		return getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos blockPos, Block neighbour) {
		updateRedstone(world, blockPos, state);
		super.neighborChanged(state, world, blockPos, neighbour);
	}

	@Override
	protected boolean onBlockAddedNextTick(World world, BlockPos blockPos, IBlockState state) {
		updateRedstone(world, blockPos, state);
		return super.onBlockAddedNextTick(world, blockPos, state);
	}

	private static void updateRedstone(World world, BlockPos blockPos, IBlockState state) {
		if (!(world instanceof WorldServer)) return;

		boolean isPowered = world.isBlockIndirectlyGettingPowered(blockPos) > 0;

		final IBlockState newState = state.withProperty(POWERED, isPowered);
		if (state != newState)
			world.setBlockState(blockPos, newState, BlockNotifyFlags.ALL);
	}
}
