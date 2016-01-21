package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import openblocks.common.tileentity.TileEntityTrophy;

public class GuardianBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(10, 0.0F));
		}
		return 100;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
