package openblocks.network;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import openblocks.Log;
import openblocks.OpenBlocks;
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

	public static Set<EntityPlayer> getPlayersInRange(World world, int blockX, int blockZ, int distance) {
		Set<EntityPlayer> playerList = new HashSet<EntityPlayer>();
		for (int j = 0; j < world.playerEntities.size(); j++) {
			EntityPlayerMP player = (EntityPlayerMP)world.playerEntities.get(j);
			if (Math.abs(player.posX - blockX) <= distance && Math.abs(player.posZ - blockZ) <= distance) {
				playerList.add(player);
			}
		}
		return playerList;
	}

	public static boolean shouldSendUpdateToPlayer(World worldObj, int sourceX, int sourceZ, Player player) {
		/* Nope for clients */
		if (worldObj.isRemote) return false;
		// Translate the Block coords to Chunk Coords
		sourceX = sourceX >> 4;
		sourceZ = sourceZ >> 4;
		IChunkProvider chunkProvider = worldObj.getChunkProvider();
		/* If the chunk doesn't exist, then return false */
		if (worldObj.getChunkProvider().chunkExists(sourceX, sourceZ)) {
			System.out.println("Chunk doesn't exist " + sourceX + "," + sourceZ);
			return false;
		}
		Chunk chunk = chunkProvider.provideChunk(sourceX, sourceZ);
		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP mpInstance = (EntityPlayerMP)player;
			if (mpInstance.loadedChunks != null && !mpInstance.loadedChunks.isEmpty()) {
				@SuppressWarnings("unchecked")
				Iterator<Chunk> iterator = mpInstance.loadedChunks.iterator();
				while (iterator.hasNext()) {
					Chunk testChunk = iterator.next();
					System.out.println("That's a TRUE for updating player");
					if (testChunk == chunk) return true;
				}
			}
		}
		System.out.println("Thats a FALSE for updating player");
		return false;
	}

	public static List<EntityPlayer> getPlayersThatNeedUpdates(World worldObj, int sourceX, int sourceZ) {
		ArrayList<EntityPlayer> playerList = new ArrayList<EntityPlayer>();
		/* Nope for clients */
		if (worldObj.isRemote) return playerList;
		// Translate the Block coords to Chunk Coords
		sourceX = sourceX >> 4;
		sourceZ = sourceZ >> 4;
		IChunkProvider chunkProvider = worldObj.getChunkProvider();
		/* If the chunk doesn't exist, then return false */
		if (worldObj.getChunkProvider().chunkExists(sourceX, sourceZ)) {
			System.out.println("Chunk doesn't exist " + sourceX + "," + sourceZ);
			return playerList;
		}
		Chunk chunk = chunkProvider.provideChunk(sourceX, sourceZ);
		@SuppressWarnings("unchecked")
		Iterator<EntityPlayer> it = worldObj.playerEntities.iterator();
		while (it.hasNext()) {
			EntityPlayer ent = it.next();
			if (ent instanceof EntityPlayerMP) {
				EntityPlayerMP mpInstance = (EntityPlayerMP)ent;
				if (mpInstance.loadedChunks != null && !mpInstance.loadedChunks.isEmpty()) {
					@SuppressWarnings("unchecked")
					Iterator<Chunk> iterator = mpInstance.loadedChunks.iterator();
					while (iterator.hasNext()) {
						Chunk testChunk = iterator.next();
						if (testChunk == chunk) {
							playerList.add(ent);
							break;
						}
					}
				}
			}
		}
		return playerList;
	}
}
