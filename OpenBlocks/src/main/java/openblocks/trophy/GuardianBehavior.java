package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import openblocks.common.tileentity.TileEntityTrophy;

public class GuardianBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)player).connection.sendPacket(new SChangeGameStatePacket(10, 0.0F));
		}
		return 100;
	}
}
