package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockScaffolding extends OpenBlock {
	public BlockScaffolding() {
		super(Material.CLOTH);
		setTickRandomly(true);
		setHardness(0.1F);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (Config.scaffoldingDespawnRate <= 0 || random.nextInt(Config.scaffoldingDespawnRate) == 0) {
			world.destroyBlock(pos, true);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		final IBlockState neighbourState = blockAccess.getBlockState(pos.offset(side));
		return neighbourState.getBlock() != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}
}
