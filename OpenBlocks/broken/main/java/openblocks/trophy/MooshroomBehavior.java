package openblocks.trophy;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class MooshroomBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		final BlockPos base = tile.getPos();
		final World world = tile.getWorld();

		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++) {
				final BlockPos pos = base.add(x, 0, z);
				if (world.isAirBlock(pos) && Blocks.BROWN_MUSHROOM.canPlaceBlockAt(world, pos)) {
					world.setBlockState(pos, Blocks.BROWN_MUSHROOM.getDefaultState());
				}
			}

		return 100;
	}
}
