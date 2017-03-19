package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockVillageHighlighter extends OpenBlock.FourDirections {

	public BlockVillageHighlighter() {
		super(Material.ROCK);
	}

	// TODO 1.8.9 you bet
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
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
}
