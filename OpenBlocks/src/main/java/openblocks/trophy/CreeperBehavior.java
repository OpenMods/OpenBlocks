package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import openblocks.common.tileentity.TileEntityTrophy;

public class CreeperBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		final BlockPos pos = tile.getPos();
		tile.getWorld().createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), 2, false);
		return 0;
	}
}
