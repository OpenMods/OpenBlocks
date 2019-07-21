package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockNotifyFlags;

@BookDocumentation
public class BlockSponge extends OpenBlock {

	private static final int TICK_RATE = 20 * 5;

	private static final int EVENT_BURN = 123;

	public BlockSponge() {
		super(Material.SPONGE);
		setSoundType(SoundType.CLOTH);
		setHarvestLevel("axe", 1);
	}

	private static int getCleanupFlags() {
		return Config.spongeBlockUpdate? BlockNotifyFlags.ALL : BlockNotifyFlags.SEND_TO_CLIENTS;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighbour, BlockPos neigbourPos) {
		clearupLiquid(world, pos);
	}

	@Override
	public int tickRate(World world) {
		return TICK_RATE;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		if (!Config.spongeBlockUpdate)
			updateNeigbouringLiquids(world, pos);
	}

	private void updateNeigbouringLiquids(World world, BlockPos pos) {
		// unfreeze liquids on cleared area border
		final int extendedRange = Config.spongeRange + 1;
		for (int dx = -extendedRange; dx <= extendedRange; dx++) {
			for (int dy = -extendedRange; dy <= extendedRange; dy++) {
				for (int dz = -extendedRange; dz <= extendedRange; dz++) {
					final BlockPos workPos = pos.add(dx, dy, dz);
					if (!world.isBlockLoaded(workPos)) continue;
					final BlockState state = world.getBlockState(workPos);
					Material material = state.getMaterial();
					if (material.isLiquid()) {
						state.neighborChanged(world, workPos, this, pos);
					}
				}
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		clearupLiquid(world, pos);
		world.scheduleUpdate(pos, this, TICK_RATE + world.rand.nextInt(5));
	}

	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random random) {
		clearupLiquid(world, pos);
		world.scheduleUpdate(pos, this, TICK_RATE + world.rand.nextInt(5));
	}

	private void clearupLiquid(World world, BlockPos pos) {
		if (world.isRemote) return;
		boolean hitLava = false;
		final int cleanupFlags = getCleanupFlags();
		for (int dx = -Config.spongeRange; dx <= Config.spongeRange; dx++) {
			for (int dy = -Config.spongeRange; dy <= Config.spongeRange; dy++) {
				for (int dz = -Config.spongeRange; dz <= Config.spongeRange; dz++) {
					final BlockPos workPos = pos.add(dx, dy, dz);
					if (!world.isBlockLoaded(workPos)) continue;
					final BlockState state = world.getBlockState(workPos);
					Material material = state.getMaterial();
					if (material.isLiquid()) {
						hitLava |= material == Material.LAVA;
						world.setBlockState(pos.add(dx, dy, dz), Blocks.AIR.getDefaultState(), cleanupFlags);
					}
				}
			}
		}
		if (hitLava) world.addBlockEvent(pos, this, EVENT_BURN, 0);
	}

	@Override
	public boolean eventReceived(BlockState state, World world, BlockPos pos, int eventId, int eventParam) {
		if (eventId == EVENT_BURN) {
			if (world.isRemote) {
				for (int i = 0; i < 20; i++) {
					double px = pos.getX() + world.rand.nextDouble() * 0.1;
					double py = pos.getY() + 1.0 + world.rand.nextDouble();
					double pz = pos.getZ() + world.rand.nextDouble();
					world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, px, py, pz, 0.0D, 0.0D, 0.0D);
				}
			} else {
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
			}
			return true;
		}

		return super.eventReceived(state, world, pos, eventId, eventParam);
	}
}
