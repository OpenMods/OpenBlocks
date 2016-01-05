package openblocks.common;

import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import openblocks.Config;
import openmods.Log;
import openmods.Mods;
import openmods.config.properties.ConfigurationChange;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MapDataManager {

	public abstract static class MapIdRequest extends NetworkEvent {
		public List<Integer> mapIds = Lists.newArrayList();

		@Override
		protected void readFromStream(PacketBuffer input) {
			final int length = input.readVarIntFromBuffer();
			for (int i = 0; i < length; i++) {
				final int id = input.readVarIntFromBuffer();
				mapIds.add(id);
			}
		}

		@Override
		protected void writeToStream(PacketBuffer output) {
			output.writeVarIntToBuffer(mapIds.size());
			for (Integer id : mapIds)
				output.writeVarIntToBuffer(id);
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
		protected void readFromStream(PacketBuffer input) {
			final int length = input.readVarIntFromBuffer();
			for (int i = 0; i < length; i++) {
				final int id = input.readVarIntFromBuffer();
				HeightMapData data = new HeightMapData(id, false);
				data.readFromStream(input);
				maps.put(id, data);
			}
		}

		@Override
		protected void writeToStream(PacketBuffer output) {
			int size = 0;
			for (HeightMapData data : maps.values())
				if (data.isValid()) size++;

			output.writeVarIntToBuffer(size);
			for (Map.Entry<Integer, HeightMapData> e : maps.entrySet()) {
				HeightMapData map = e.getValue();
				if (map.isValid()) {
					output.writeVarIntToBuffer(e.getKey());
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

		final MapDataResponseEvent response = new MapDataResponseEvent();
		final TIntSet missingMaps = new TIntHashSet();
		for (Integer mapId : evt.mapIds) {
			final HeightMapData map = getMapData(world, mapId);
			if (map != null && !map.isEmpty()) response.maps.put(mapId, map);
			else missingMaps.add(mapId);
		}

		if (!missingMaps.isEmpty()) {
			boolean lessThan16 = missingMaps.forEach(new TIntProcedure() {
				@Override
				public boolean execute(int value) {
					return value < 16;
				}
			});

			// NEI asks for items with damage 0..15 and I can't block it via API
			if (Config.alwaysReportInvalidMapRequests || !lessThan16 || !Loader.isModLoaded(Mods.NOTENOUGHITEMS)) Log.info("Player %s asked for non-existent maps %s", evt.sender, missingMaps.toString());
		}

		if (!evt.mapIds.isEmpty()) evt.reply(response);
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
					else Log.warn("Can't find block %s", entry);
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
