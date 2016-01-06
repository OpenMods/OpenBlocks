package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import openblocks.common.tileentity.TileEntityTrophy;

public class CreeperBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		final BlockPos pos = tile.getPos();
		tile.getWorld().createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), 2, false);
		return 0;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
