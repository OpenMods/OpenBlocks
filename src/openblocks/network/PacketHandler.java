package openblocks.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.Player;
import openblocks.*;
import openmods.Log;
import openmods.network.EventPacket;
import openmods.network.PacketHandlerBase;

public class PacketHandler extends PacketHandlerBase {

	public final static String CHANNEL_SYNC = "OpenBlocks|S";
	public final static String CHANNEL_EVENTS = "OpenBlocks|E";

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

		try {
			if (packet.channel.equals(CHANNEL_SYNC)) {
				OpenBlocks.syncableManager.handlePacket(packet);
			} else if (packet.channel.equals(CHANNEL_EVENTS)) {
				EventPacket event = EventPacket.deserializeEvent(packet);
				event.manager = manager;
				event.player = player;
				MinecraftForge.EVENT_BUS.post(event);
			}
		} catch (Exception e) {
			Log.warn(e, "Error while handling data on channel %s from player '%s'", packet.channel, player);
		}
	}

	@Override
	public String getSyncChannel() {
		return CHANNEL_SYNC;
	}

	@Override
	public String getEventChannel() {
		return CHANNEL_EVENTS;
	}
	
}
