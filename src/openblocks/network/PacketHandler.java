package openblocks.network;

import openblocks.OpenBlocks;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

		if (packet.channel.equals("OpenBlocks")) {
			try {
				OpenBlocks.syncableManager.handlePacket(packet);
			} catch (Exception e) {
			}
		}
	}
}
