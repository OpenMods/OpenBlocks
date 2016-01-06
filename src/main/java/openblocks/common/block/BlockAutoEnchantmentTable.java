package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockAutoEnchantmentTable extends OpenBlock {

	public BlockAutoEnchantmentTable() {
		super(Material.rock);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.randomDisplayTick(world, pos, state, rand);

		for (int x = -2; x <= 2; ++x) {
			for (int z = -2; z <= 2; ++z) {
				if (x > -2 && x < 2 && z == -1) z = 2;

				if (rand.nextInt(16) == 0) {
					for (int y = 0; y <= 1; ++y) {
						final BlockPos blockpos = pos.add(x, y, z);

						if (world.getBlockState(blockpos).getBlock() == Blocks.bookshelf) {
							if (!world.isAirBlock(pos.add(x / 2, 0, z / 2))) break;
							world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5D, pos.getY() + 2.0D, pos.getZ() + 0.5D, x + rand.nextFloat() - 0.5D, y - rand.nextFloat() - 1.0F, z + rand.nextFloat() - 0.5D, new int[0]);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

}
