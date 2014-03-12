package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import openblocks.common.tileentity.TileEntityTrophy;

public class BlazeBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		player.setFire(4);
		return 0;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
