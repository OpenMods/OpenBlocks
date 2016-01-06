package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockSponge extends OpenBlock {

	private static final int TICK_RATE = 20 * 5;
	private static final Random RANDOM = new Random();

	public BlockSponge() {
		super(Material.sponge);
		setStepSound(soundTypeCloth);
		setTickRandomly(true);
		setHarvestLevel("axe", 1);
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		clearupLiquid(world, pos);
	}

	@Override
	public int tickRate(World world) {
		return TICK_RATE;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		clearupLiquid(world, pos);
		world.scheduleUpdate(pos, this, TICK_RATE + RANDOM.nextInt(5));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		clearupLiquid(world, pos);
		world.scheduleUpdate(pos, this, TICK_RATE + RANDOM.nextInt(5));
	}

	private void clearupLiquid(World world, BlockPos pos) {
		if (world.isRemote) return;
		boolean hitLava = false;
		for (int dx = -Config.spongeRange; dx <= Config.spongeRange; dx++) {
			for (int dy = -Config.spongeRange; dy <= Config.spongeRange; dy++) {
				for (int dz = -Config.spongeRange; dz <= Config.spongeRange; dz++) {
					final BlockPos workPos = pos.add(dx, dy, dz);
					Block block = world.getBlockState(workPos).getBlock();
					Material material = block.getMaterial();
					if (material.isLiquid()) {
						hitLava |= material == Material.lava;
						world.setBlockToAir(pos);
					}
				}
			}
		}
		if (hitLava) world.addBlockEvent(pos, this, 0, 0);
	}

	@Override
	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventId, int eventParam) {
		if (world.isRemote) {
			for (int i = 0; i < 20; i++) {
				double px = pos.getX() + RANDOM.nextDouble() * 0.1;
				double py = pos.getY() + 1.0 + RANDOM.nextDouble();
				double pz = pos.getZ() + RANDOM.nextDouble();
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, px, py, pz, 0.0D, 0.0D, 0.0D);
			}
		} else {
			world.setBlockState(pos, Blocks.fire.getDefaultState());
		}
		return true;
	}
}
