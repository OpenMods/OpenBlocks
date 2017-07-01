package openblocks.client.renderer.block.canvas;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Table;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader.White;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.renderer.TextureUploader;
import openblocks.client.renderer.TextureUploader.IUploadableTexture;
import openmods.Log;

public class CanvasTextureManager {

	private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("openblocks.debugCanvasTextures", "false"));

	public static final CanvasTextureManager INSTANCE = new CanvasTextureManager();

	private int peakRejectedAllocations = 0;

	public int getPeakRejectedAllocations() {
		return peakRejectedAllocations;
	}

	private CanvasTextureManager() {}

	private static class EmptyTextureData {
		final Map<Integer, int[][]> mipmapLevels = Maps.newHashMap();

		public int[][] getContents(int mipmapLevel) {
			int[][] contents = mipmapLevels.get(mipmapLevel);
			if (contents == null) {
				contents = generateEmptyMipmap(mipmapLevel);
				mipmapLevels.put(mipmapLevel, contents);
			}

			return contents;
		}

		private static int[][] generateEmptyMipmap(int mipmapLevel) {
			final int[][] result = new int[mipmapLevel + 1][];

			int size = CanvasLayer.TEXTURE_WIDTH * CanvasLayer.TEXTURE_HEIGHT;
			for (int i = 0; i <= mipmapLevel; i++) {
				result[i] = new int[size];
				size >>= 2;
			}

			return result;
		}
	}

	private class CanvasTexture extends TextureAtlasSprite implements IUploadableTexture {
		public final ResourceLocation location;

		private final EmptyTextureData emptyTexture;

		public int referenceCount = 0;

		private int mipmapLevels;

		private boolean requiresUpload;

		public CanvasTexture(ResourceLocation location, EmptyTextureData emptyTexture) {
			super(location.toString());
			this.location = location;
			this.width = CanvasLayer.TEXTURE_WIDTH;
			this.height = CanvasLayer.TEXTURE_HEIGHT;
			this.emptyTexture = emptyTexture;
		}

		@Override
		public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
			return true;
		}

		@Override
		public boolean load(IResourceManager manager, ResourceLocation location) {
			return false;
		}

		@Override
		public void generateMipmaps(int level) {
			clearFramesTextureData();
			framesTextureData.add(emptyTexture.getContents(level));
			this.mipmapLevels = level;
		}

		public void prepareTexture(int background, List<CanvasLayer> layers) {
			if (!layers.isEmpty() && layers.get(0).orientation != TextureOrientation.R0)
				Log.warn("Unoptimized texture: %s!", layers);

			clearFramesTextureData();
			final int[][] mipmaps = new int[this.mipmapLevels + 1][];
			final int size = CanvasLayer.TEXTURE_WIDTH * CanvasLayer.TEXTURE_HEIGHT;
			final int[] contents = mipmaps[0] = new int[size];
			for (int i = 0; i < size; i++) {
				int color = background;
				for (CanvasLayer layer : layers) {
					final int transformedIndex = layer.orientation.rotate16x16(i);
					color = layer.pattern.mix(transformedIndex, layer.color, color);
				}
				contents[i] = color;
			}

			framesTextureData.add(mipmaps);
			super.generateMipmaps(this.mipmapLevels);

			requiresUpload = true;
			TextureUploader.INSTANCE.scheduleTextureUpload(this);
		}

		@Override
		public void upload() {
			if (requiresUpload) {
				requiresUpload = false;
				TextureUtil.uploadTextureMipmap(this.framesTextureData.get(0), this.width, this.height, this.originX, this.originY, false, false);
			}
		}
	}

	private final Deque<CanvasTexture> freeTextures = Queues.newArrayDeque();

	private final Table<Integer, List<CanvasLayer>, CanvasTexture> usedTextures = HashBasedTable.create();

	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent.Pre evt) {
		freeTextures.clear();
		usedTextures.clear();
		peakRejectedAllocations = 0;

		CanvasSideState.onTextureReload();

		if (DEBUG) Log.info("Allocating %s textures", Config.canvasPoolSize);

		final TextureMap map = evt.getMap();
		final EmptyTextureData emptyTexture = new EmptyTextureData();
		for (int i = 0; i < Config.canvasPoolSize; i++) {
			final CanvasTexture entry = new CanvasTexture(OpenBlocks.location("canvas-" + i), emptyTexture);
			map.setTextureEntry(entry);
			freeTextures.push(entry);
		}
	}

	public ResourceLocation getTexture(int background, List<CanvasLayer> layers) {
		CanvasTexture allocatedTexture = usedTextures.get(background, layers);
		if (allocatedTexture != null) {
			allocatedTexture.referenceCount++;
			if (DEBUG) Log.info("Incrementing texture %s [%08X:%s]. counter = %d", allocatedTexture.location, background, layers, allocatedTexture.referenceCount);
			return allocatedTexture.location;
		}

		allocatedTexture = freeTextures.poll();
		if (allocatedTexture == null) {
			// TODO pool's empty, clean unreferenced, suggest reload
			peakRejectedAllocations++;
			if (DEBUG) Log.info("Can't load texture [%08X:%s]", background, layers);
			return White.LOCATION;
		}

		peakRejectedAllocations = 0;
		allocatedTexture.prepareTexture(background, layers);
		usedTextures.put(background, layers, allocatedTexture);
		allocatedTexture.referenceCount++;

		if (DEBUG) Log.info("Loaded texture %s [%08X:%s]. counter = %d", allocatedTexture.location, background, layers, allocatedTexture.referenceCount);

		return allocatedTexture.location;
	}

	public void releaseTexture(int background, List<CanvasLayer> layers) {
		CanvasTexture textureToRelease = usedTextures.get(background, layers);
		Preconditions.checkNotNull(textureToRelease, "Texture not allocated");

		--textureToRelease.referenceCount;

		if (DEBUG) Log.info("Decrementing texture %s [%08X:%s]. counter = %d", textureToRelease.location, background, layers, textureToRelease.referenceCount);
	}
}
