package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class MooshroomBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		final BlockPos base = tile.getPos();
		final World world = tile.getWorld();

		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++) {
				final BlockPos pos = base.add(x, 0, z);
				if (world.isAirBlock(pos)
						&& Blocks.brown_mushroom.canPlaceBlockAt(world, pos)) {
					world.setBlockState(pos, Blocks.brown_mushroom.getDefaultState());
				}
			}

		return 100;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
