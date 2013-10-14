package openblocks.network;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.WorldServer;
import openblocks.Log;
import openblocks.OpenBlocks;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

		if (packet.channel.equals("OpenBlocks")) {
			try {
				OpenBlocks.syncableManager.handlePacket(packet);
			} catch (Exception e) {
				Log.warn(e, "Error while handling data from player '%s'", player);
			}
		}
	}

	public static void sendPacketToPlayer(EntityPlayer player, Packet packetToSend) {
		Preconditions.checkArgument(player instanceof EntityPlayerMP, "This method can be only used on server side");
		((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packetToSend);
	}

	public static Set<EntityPlayer> getPlayersWatchingChunk(WorldServer world, int chunkX, int chunkZ) {
		PlayerManager manager = world.getPlayerManager();

		Set<EntityPlayer> playerList = Sets.newHashSet();
		for (Object o : world.playerEntities) {
			EntityPlayerMP player = (EntityPlayerMP)o;
			if (manager.isPlayerWatchingChunk(player, chunkX, chunkZ)) playerList.add(player);
		}
		return playerList;
	}

	public static Set<EntityPlayer> getPlayersWatchingBlock(WorldServer world, int blockX, int blockZ) {
		return getPlayersWatchingChunk(world, blockX >> 4, blockZ >> 4);
	}
}
