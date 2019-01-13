package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityStatus;
import openblocks.common.tileentity.TileEntityTrophy;

public class EvocationBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP)player).connection.sendPacket(new SPacketEntityStatus(player, (byte)35));
		}
		return 100;
	}
}
