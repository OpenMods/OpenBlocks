package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockVillageHighlighter extends OpenBlock.FourDirections {

	public BlockVillageHighlighter() {
		super(Material.rock);
	}

	// TODO 1.8.9 you bet
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		TileEntityVillageHighlighter tile = getTileEntity(world, pos, TileEntityVillageHighlighter.class);
		return (tile != null)? tile.getSignalStrength() : 0;
	}

	@Override
	public int getStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		// TODO Side-aware?
		return getWeakPower(world, pos, state, side);
	}
}
