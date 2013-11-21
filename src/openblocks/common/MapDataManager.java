package openblocks.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.Log;
import openblocks.OpenBlocks;
import openmods.network.EventPacket;
import openmods.utils.ByteUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.Player;

public class MapDataManager {

	public abstract static class MapIdRequest extends EventPacket {
		public List<Integer> mapIds = Lists.newArrayList();

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			int length = ByteUtils.readVLI(input);
			for (int i = 0; i < length; i++) {
				int id = ByteUtils.readVLI(input);
				mapIds.add(id);
			}
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			ByteUtils.writeVLI(output, mapIds.size());
			for (Integer id : mapIds)
				ByteUtils.writeVLI(output, id);
		}
	}

	public static class MapDataRequestEvent extends MapIdRequest {
		@Override
		public EventType getType() {
			return EventType.MAP_DATA_REQUEST;
		}
	}

	public static class MapUpdatesEvent extends MapIdRequest {
		@Override
		public EventType getType() {
			return EventType.MAP_UPDATES;
		}
	}

	public static class MapDataResponseEvent extends EventPacket {

		public Map<Integer, HeightMapData> maps = Maps.newHashMap();

		@Override
		public EventType getType() {
			return EventType.MAP_DATA_RESPONSE;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			int length = ByteUtils.readVLI(input);
			for (int i = 0; i < length; i++) {
				int id = ByteUtils.readVLI(input);
				HeightMapData data = new HeightMapData(id, false);
				data.readFromStream(input);
				maps.put(id, data);
			}
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			int size = 0;
			for (HeightMapData data : maps.values())
				if (data.isValid()) size++;

			ByteUtils.writeVLI(output, size);
			for (Map.Entry<Integer, HeightMapData> e : maps.entrySet()) {
				HeightMapData map = e.getValue();
				if (map.isValid()) {
					ByteUtils.writeVLI(output, e.getKey());
					map.writeToStream(output);
				} else Log.warn("Trying to propagate invalid map data %d", e.getKey());
			}
		}
	}

	public final static MapDataManager instance = new MapDataManager();

	private Set<Integer> mapsToUpdate = Sets.newHashSet();

	public static int createNewMap(World world, byte scale) {
		int id = world.getUniqueDataId("height_map");
		HeightMapData data = new HeightMapData(id, false);
		data.scale = scale;
		data.markDirty();
		world.setItemData(data.mapName, data);
		return id;
	}

	public static HeightMapData getMapData(World world, int mapId) {
		if (mapId < 0) return HeightMapData.INVALID;

		String name = HeightMapData.getMapName(mapId);
		HeightMapData result = (HeightMapData)world.loadItemData(HeightMapData.class, name);

		return result != null? result : HeightMapData.EMPTY;
	}

	public static void setMapData(World world, HeightMapData data) {
		world.setItemData(data.mapName, data);
	}

	private static World getPlayerWorld(Player player) {
		return ((EntityPlayer)player).worldObj;
	}

	@ForgeSubscribe
	public void onMapDataRequest(MapDataRequestEvent evt) {
		World world = getPlayerWorld(evt.player);

		MapDataResponseEvent response = new MapDataResponseEvent();
		for (Integer mapId : evt.mapIds) {
			HeightMapData map = getMapData(world, mapId);
			if (map != null) {
				response.maps.put(mapId, map);
			} else {
				Log.info("Player %s asked for non-existent map %d", evt.player, mapId);
			}
		}

		evt.reply(response);
	}

	@ForgeSubscribe
	public void onMapDataResponse(MapDataResponseEvent evt) {
		World world = getPlayerWorld(evt.player);

		for (Map.Entry<Integer, HeightMapData> e : evt.maps.entrySet()) {
			HeightMapData mapData = e.getValue();
			world.setItemData(mapData.mapName, mapData);
		}
	}

	@ForgeSubscribe
	public void onMapUpdates(MapUpdatesEvent evt) {
		World world = getPlayerWorld(evt.player);

		Set<Integer> mapsToUpdate = Sets.newHashSet();
		for (Integer mapId : evt.mapIds) {
			HeightMapData map = getMapData(world, mapId);
			if (map != null) mapsToUpdate.add(mapId);
		}

		if (!mapsToUpdate.isEmpty()) {
			MapDataRequestEvent request = new MapDataRequestEvent();
			request.mapIds = Lists.newArrayList(mapsToUpdate);
			evt.reply(request);
		}
	}

	@SuppressWarnings("unchecked")
	public void sendUpdates(MinecraftServer server) {
		if (mapsToUpdate.isEmpty()) return;

		MapUpdatesEvent evt = new MapUpdatesEvent();
		evt.mapIds.addAll(mapsToUpdate);
		mapsToUpdate.clear();

		Packet toSend = EventPacket.serializeEvent(evt);
		List<EntityPlayer> players = server.getConfigurationManager().playerEntityList;
		for (EntityPlayer player : players)
			OpenBlocks.proxy.sendPacketToPlayer((Player)player, toSend);
	}

	public void markDataUpdated(World world, int mapId) {
		HeightMapData data = getMapData(world, mapId);
		data.markDirty();
		mapsToUpdate.add(mapId);
	}

	public static void requestMapData(World world, int mapId) {
		if (world.isRemote) {
			MapDataRequestEvent evt = new MapDataRequestEvent();
			evt.mapIds.add(mapId);
			evt.sendToServer();

			HeightMapData stub = new HeightMapData(mapId, true);
			world.setItemData(stub.mapName, stub);
		}
	}
}
