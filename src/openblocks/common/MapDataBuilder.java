package openblocks.common;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import openblocks.common.HeightMapData.LayerData;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemHeightMap;
import openmods.utils.BitSet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

public class MapDataBuilder {
	private static final int LAYER_TERRAIN = 0;
	private static final int LAYER_LIQUIDS = 1;
	private static final int LAYER_COUNT = 2;

	public final int mapId;
	private HeightMapData data;

	private static class BlockCount {
		public byte groundColor;
		public int groundHeight;

		public byte liquidColor;
		public int liquidHeight;

		private static Block getValidBlock(Chunk chunk, int x, int y, int z) {
			int blockId = chunk.getBlockID(x, y, z);
			if (blockId == 0) return null;

			Block block = Block.blocksList[blockId];
			if (block == null) return null;

			if (block.blockMaterial.materialMapColor.colorIndex == 0) return null;

			if (!MapDataManager.instance.isBlockTransparent(block)) return null;

			return block;
		}

		public void average(Chunk chunk, int startX, int startZ, int size) {
			double groundHeightSum = 0;
			int[] groundColors = new int[MapColor.mapColorArray.length];

			double liquidHeightSum = 0;
			int liquidCount = 0;
			int[] liquidColors = new int[MapColor.mapColorArray.length];

			for (int x = startX; x < startX + size; x++)
				for (int z = startZ; z < startZ + size; z++) {
					Block blockLiquid = null;
					int heightLiquid = 0;

					Block blockSolid = null;
					int heightSolid = 0;

					for (int y = 255; y >= 0; y--) {
						Block block = getValidBlock(chunk, x, y, z);
						if (block == null) continue;

						if (block.blockMaterial.isLiquid()) {
							if (blockLiquid == null) {
								blockLiquid = block;
								heightLiquid = y;
							}
						} else {
							blockSolid = block;
							heightSolid = y;
							break;
						}
					}

					if (blockSolid != null) {
						groundHeightSum += heightSolid;
						int color = blockSolid.blockMaterial.materialMapColor.colorIndex;
						groundColors[color]++;
					}

					if (blockLiquid != null) {
						liquidHeightSum += heightLiquid;
						int color = blockLiquid.blockMaterial.materialMapColor.colorIndex;
						liquidColors[color]++;
						liquidCount++;
					}
				}

			{
				int maxColorCount = -1;
				for (int i = 0; i < groundColors.length; i++)
					if (groundColors[i] > maxColorCount) {
						groundColor = (byte)i;
						maxColorCount = groundColors[i];
					}
				groundHeight = (int)(groundHeightSum / (size * size));
			}

			if (liquidCount > size * size / 2) {
				int maxColorCount = -1;
				for (int i = 0; i < liquidColors.length; i++)
					if (liquidColors[i] > maxColorCount) {
						liquidColor = (byte)i;
						maxColorCount = liquidColors[i];
					}

				liquidHeight = (int)(liquidHeightSum / liquidCount);
			}
		}
	}

	public class ChunkJob {
		public final ChunkCoordIntPair chunk;
		public final int pixelsPerChunk;
		public final int mapMinX;
		public final int mapMinY;
		public final int bitNum;

		private ChunkJob(ChunkCoordIntPair chunk, int pixelsPerChunk, int mapMinX, int mapMinY, int bitNum) {
			this.chunk = chunk;
			this.pixelsPerChunk = pixelsPerChunk;
			this.mapMinX = mapMinX;
			this.mapMinY = mapMinY;
			this.bitNum = bitNum;
		}

		private void mapChunk(World world, Chunk chunk) {
			LayerData ground = data.layers[LAYER_TERRAIN];
			LayerData liquid = data.layers[LAYER_LIQUIDS];

			final int blocksPerPixel = 16 / pixelsPerChunk;

			int blockInChunkX = 0;
			for (int mapX = mapMinX; mapX < mapMinX + pixelsPerChunk; mapX++) {
				int blockInChunkZ = 0;
				for (int mapY = mapMinY; mapY < mapMinY + pixelsPerChunk; mapY++) {
					BlockCount count = new BlockCount();
					count.average(chunk, blockInChunkX, blockInChunkZ, blocksPerPixel);

					int index = mapY * 64 + mapX;

					ground.colorMap[index] = count.groundColor;
					ground.heightMap[index] = (byte)(count.groundHeight);

					liquid.colorMap[index] = count.liquidColor;
					liquid.heightMap[index] = (byte)(count.liquidHeight);

					blockInChunkZ += blocksPerPixel;
				}
				blockInChunkX += blocksPerPixel;
			}
			MapDataManager.instance.markDataUpdated(world, mapId);
		}
	}

	public MapDataBuilder(int mapId) {
		this.mapId = mapId;
	}

	public void loadMap(World world) {
		this.data = MapDataManager.getMapData(world, mapId);
	}

	public void resetMap(World world, int x, int z) {
		this.data = MapDataManager.getMapData(world, mapId);
		data.centerX = ((x >> 4) << 4);
		data.centerZ = ((z >> 4) << 4);
		data.dimension = world.provider.dimensionId;

		if (data.layers == null || data.layers.length != LAYER_COUNT) data.layers = new HeightMapData.LayerData[LAYER_COUNT];

		LayerData ground = data.layers[LAYER_TERRAIN];

		if (ground == null) {
			ground = new LayerData();
			data.layers[LAYER_TERRAIN] = ground;
		}
		ground.alpha = (byte)255;

		LayerData liquid = data.layers[LAYER_LIQUIDS];

		if (liquid == null) {
			liquid = new LayerData();
			data.layers[LAYER_LIQUIDS] = liquid;
		}
		liquid.alpha = (byte)128;
		MapDataManager.instance.markDataUpdated(world, mapId);
	}

	public Set<ChunkJob> createJobs(BitSet finishedChunks) {
		Preconditions.checkState(data != null, "Invalid usage, load map first");

		Map<ChunkCoordIntPair, ChunkJob> result = Maps.newHashMap();
		final int blocksPerPixel = (1 << data.scale);
		final int pixelsPerChunk = 16 / blocksPerPixel;
		final int chunksPerSide = 64 / pixelsPerChunk;

		int middleChunkX = data.centerX >> 4;
		int middleChunkZ = data.centerZ >> 4;

		int bitNum = 0;
		for (int mapX = 0, chunkX = middleChunkX - chunksPerSide / 2; chunkX < middleChunkX + chunksPerSide / 2; mapX += pixelsPerChunk, chunkX++)
			for (int mapY = 0, chunkZ = middleChunkZ - chunksPerSide / 2; chunkZ < middleChunkZ + chunksPerSide / 2; mapY += pixelsPerChunk, chunkZ++) {
				ChunkCoordIntPair chunk = new ChunkCoordIntPair(chunkX, chunkZ);
				if (!finishedChunks.testBit(bitNum)) {
					result.put(chunk, new ChunkJob(chunk, pixelsPerChunk, mapX, mapY, bitNum));
				}
				bitNum++;
			}

		return Sets.newHashSet(result.values());
	}

	private static class JobDistance implements Comparable<JobDistance> {
		public final double distance;
		public final ChunkJob job;

		public JobDistance(double distance, ChunkJob job) {
			this.distance = distance;
			this.job = job;
		}

		@Override
		public int compareTo(JobDistance o) {
			return Double.compare(distance, o.distance);
		}
	}

	public static ChunkJob doNextChunk(World world, double x, double z, Collection<ChunkJob> jobs) {
		if (jobs.isEmpty()) return null;

		PriorityQueue<JobDistance> distances = Queues.newPriorityQueue();

		for (ChunkJob job : jobs) {
			ChunkCoordIntPair chunk = job.chunk;
			double dx = chunk.getCenterXPos() - x;
			double dz = chunk.getCenterZPosition() - z;
			distances.add(new JobDistance(dx * dx + dz * dz, job));
		}

		IChunkProvider provider = world.getChunkProvider();
		while (!distances.isEmpty()) {
			JobDistance dist = distances.poll();
			ChunkJob job = dist.job;
			ChunkCoordIntPair chunkCoord = job.chunk;

			if (provider.chunkExists(chunkCoord.chunkXPos, chunkCoord.chunkZPos)) {
				Chunk chunk = provider.loadChunk(chunkCoord.chunkXPos, chunkCoord.chunkZPos);
				job.mapChunk(world, chunk);
				return job;
			}
		}

		return null;
	}

	public static ItemStack upgradeToMap(World world, ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemHeightMap) return stack;
		else if (item instanceof ItemEmptyMap) {
			return ItemEmptyMap.upgradeToMap(world, stack);
		} else throw new IllegalArgumentException("Invalid item type: " + item);
	}

	public int size() {
		return 4 << data.scale;
	}

	private int neededBits() {
		int line = size();
		return line * line;
	}

	public void resizeIfNeeded(BitSet bitmap) {
		int needed = neededBits();
		if (!bitmap.checkSize(needed)) bitmap.resize(needed);
	}

	public void resize(BitSet bitmap) {
		bitmap.resize(neededBits());
	}
}
