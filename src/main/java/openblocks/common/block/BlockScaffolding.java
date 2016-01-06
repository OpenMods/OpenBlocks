package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
		super(Material.cloth);
		setTickRandomly(true);
		setHardness(0.1F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (Config.scaffoldingDespawnRate <= 0 || random.nextInt(Config.scaffoldingDespawnRate) == 0) {
			world.destroyBlock(pos, true);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
		// Code adapted from BlockBreakable
		final IBlockState iblockstate = world.getBlockState(pos);
		final Block block = iblockstate.getBlock();

		if (world.getBlockState(pos.offset(side.getOpposite())) != iblockstate) return true;

		if (block == this) return false;
		return super.shouldSideBeRendered(world, pos, side);
	}
}
