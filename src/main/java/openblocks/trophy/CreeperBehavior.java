package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import openblocks.common.tileentity.TileEntityTrophy;

public class CreeperBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		tile.getWorldObj().createExplosion(player, tile.xCoord, tile.yCoord, tile.zCoord, 2, false);
		return 0;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
