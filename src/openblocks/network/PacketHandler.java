package openblocks.network;

import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import openblocks.Log;
import openblocks.OpenBlocks;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class PacketHandler implements IPacketHandler {

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

	private static Field trackingPlayers;

	public static Set<EntityPlayer> getPlayersWatchingEntity(WorldServer server, int entityId) {
		EntityTracker tracker = server.getEntityTracker();

		if (trackingPlayers == null) trackingPlayers = ReflectionHelper.findField(EntityTracker.class, "trackedEntityIDs", "field_72794_c");

		IntHashMap trackers;
		try {
			trackers = (IntHashMap)trackingPlayers.get(tracker);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

		EntityTrackerEntry entry = (EntityTrackerEntry)trackers.lookup(entityId);

		@SuppressWarnings({ "unchecked" })
		Set<EntityPlayer> trackingPlayers = entry.trackingPlayers;

		return ImmutableSet.copyOf(trackingPlayers);
	}
}
