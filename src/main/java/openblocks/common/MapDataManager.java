package openblocks.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.Log;
import openmods.config.properties.ConfigurationChange;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;
import openmods.utils.ByteUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class MapDataManager {

	public abstract static class MapIdRequest extends NetworkEvent {
		public List<Integer> mapIds = Lists.newArrayList();

		@Override
		protected void readFromStream(DataInput input) {
			int length = ByteUtils.readVLI(input);
			for (int i = 0; i < length; i++) {
				int id = ByteUtils.readVLI(input);
				mapIds.add(id);
			}
		}

		@Override
		protected void writeToStream(DataOutput output) {
			ByteUtils.writeVLI(output, mapIds.size());
			for (Integer id : mapIds)
				ByteUtils.writeVLI(output, id);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class MapDataRequestEvent extends MapIdRequest {}

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class MapUpdatesEvent extends MapIdRequest {}

	@NetworkEventMeta(direction = EventDirection.S2C, compressed = true)
	public static class MapDataResponseEvent extends NetworkEvent {
		public Map<Integer, HeightMapData> maps = Maps.newHashMap();

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
				} else Log.debug("Trying to propagate invalid map data %d", e.getKey());
			}
		}
	}

	public final static MapDataManager instance = new MapDataManager();

	private Set<Block> blockBlacklist;

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

	@SubscribeEvent
	public void onMapDataRequest(MapDataRequestEvent evt) {
		World world = evt.sender.worldObj;

		MapDataResponseEvent response = new MapDataResponseEvent();
		for (Integer mapId : evt.mapIds) {
			HeightMapData map = getMapData(world, mapId);
			if (map != null) {
				response.maps.put(mapId, map);
			} else {
				Log.info("Player %s asked for non-existent map %d", evt.sender, mapId);
			}
		}

		evt.reply(response);
	}

	@SubscribeEvent
	public void onMapDataResponse(MapDataResponseEvent evt) {
		World world = evt.sender.worldObj;

		for (Map.Entry<Integer, HeightMapData> e : evt.maps.entrySet()) {
			HeightMapData mapData = e.getValue();
			world.setItemData(mapData.mapName, mapData);
		}
	}

	@SubscribeEvent
	public void onMapUpdates(MapUpdatesEvent evt) {
		World world = evt.sender.worldObj;

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

	public void sendUpdates(MinecraftServer server) {
		if (mapsToUpdate.isEmpty()) return;

		MapUpdatesEvent evt = new MapUpdatesEvent();
		evt.mapIds.addAll(mapsToUpdate);
		mapsToUpdate.clear();

		evt.sendToAll();
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

	private Set<Block> getBlacklist() {
		if (blockBlacklist == null) {
			blockBlacklist = Sets.newIdentityHashSet();
			for (String entry : Config.mapBlacklist) {
				try {
					String[] parts = entry.split(":");
					Preconditions.checkState(parts.length == 2);
					String modId = parts[0];
					String blockName = parts[1];

					Block block = GameRegistry.findBlock(modId, blockName);

					if (block != Blocks.air) blockBlacklist.add(block);
					else Log.info("Can't find block %s", entry);
				} catch (Throwable t) {
					Log.warn(t, "Invalid entry in map blacklist: %s", entry);
				}
			}
		}

		return blockBlacklist;
	}

	@SubscribeEvent
	public void onReconfig(ConfigurationChange.Post evt) {
		if (evt.check("cartographer", "blockBlacklist")) blockBlacklist = null;
	}

	public boolean isBlockTransparent(Block block) {
		return getBlacklist().contains(block);
	}
}
