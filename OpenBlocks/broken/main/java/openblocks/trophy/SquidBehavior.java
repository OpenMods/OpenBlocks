package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class SquidBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		final BlockPos base = tile.getPos().up();
		final World world = tile.getWorld();

		if (world.provider.doesWaterVaporize()) world.playSound(null, base, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F);
		else if (world.isAirBlock(base)) world.setBlockState(base, Blocks.FLOWING_WATER.getDefaultState());

		return 10;
	}
}
