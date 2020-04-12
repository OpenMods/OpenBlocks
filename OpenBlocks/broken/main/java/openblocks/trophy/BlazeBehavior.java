package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import openblocks.common.tileentity.TileEntityTrophy;

public class BlazeBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		player.setFire(4);
		return 0;
	}
}
