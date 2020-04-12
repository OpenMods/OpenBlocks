package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SEntityStatusPacket;
import openblocks.common.tileentity.TileEntityTrophy;

public class EvocationBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)player).connection.sendPacket(new SEntityStatusPacket(player, (byte)35));
		}
		return 100;
	}
}
