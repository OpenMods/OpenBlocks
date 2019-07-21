package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
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

	public static class Item extends BlockItem {
		public Item(Block block) {
			super(block);
		}

		@Override
		public int getItemBurnTime(ItemStack itemStack) {
			return 100;
		}
	}

	public BlockScaffolding() {
		super(Material.CLOTH);
		setTickRandomly(true);
		setHardness(0.1F);
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public void randomTick(World world, BlockPos pos, BlockState state, Random random) {
		if (Config.scaffoldingDespawnRate <= 0 || random.nextInt(Config.scaffoldingDespawnRate) == 0) {
			world.destroyBlock(pos, true);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public boolean shouldSideBeRendered(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, Direction side) {
		final BlockState neighbourState = blockAccess.getBlockState(pos.offset(side));
		return neighbourState.getBlock() != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}
}
