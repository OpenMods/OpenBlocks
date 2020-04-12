package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockAutoEnchantmentTable extends OpenBlock {

	public BlockAutoEnchantmentTable() {
		super(Material.ROCK);
	}

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == Direction.DOWN? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
		super.randomDisplayTick(state, world, pos, rand);

		for (int x = -2; x <= 2; ++x) {
			for (int z = -2; z <= 2; ++z) {
				if (x > -2 && x < 2 && z == -1) z = 2;

				if (rand.nextInt(16) == 0) {
					for (int y = 0; y <= 1; ++y) {
						final BlockPos blockpos = pos.add(x, y, z);

						if (net.minecraftforge.common.ForgeHooks.getEnchantPower(world, blockpos) > 0) {
							if (world.isAirBlock(pos.add(x / 2, 0, z / 2))) {
								world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,
										pos.getX() + 0.5D,
										pos.getY() + 2.0D,
										pos.getZ() + 0.5D,
										x + rand.nextFloat() - 0.5D,
										y - rand.nextFloat() - 1.0F,
										z + rand.nextFloat() - 0.5D);
							}
						}
					}
				}
			}
		}
	}

}
