package openblocks.client.renderer.item.stencil;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.client.renderer.TextureUploader;
import openblocks.common.IStencilPattern;
import openmods.Log;

public class StencilTextureManager {

	public static final StencilTextureManager INSTANCE = new StencilTextureManager();

	private StencilTextureManager() {}

	private static class TexturePool {

		private class PoolTexture extends TextureAtlasSprite {

			private final ResourceLocation selfLocation;

			private int mipmapLevels;

			private boolean queuedForUpload;

			private IStencilPattern pattern;

			private PoolTexture(ResourceLocation selfLocation, int mipmapLevels) {
				super(selfLocation.toString());
				this.selfLocation = selfLocation;
				this.mipmapLevels = mipmapLevels;
			}

			@Override
			public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
				return true;
			}

			private void copyTextureDataFromPrimer() {
				clearFramesTextureData();
				framesTextureData.add(backgroundSprite.getFrameTextureData(0));
			}

			@Override
			public Collection<ResourceLocation> getDependencies() {
				return ImmutableSet.of(background);
			}

			@Override
			public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
				if (backgroundSprite == null)
					backgroundSprite = textureGetter.apply(background);

				width = backgroundSprite.getIconWidth();
				height = backgroundSprite.getIconHeight();

				copyTextureDataFromPrimer();

				return false;
			}

			@Override
			public void generateMipmaps(int level) {
				this.mipmapLevels = level;
				copyTextureDataFromPrimer();
			}

			public void loadPattern(IStencilPattern pattern) {
				if (bitmap == null)
					bitmap = new StencilableBitmap(backgroundSprite.getFrameTextureData(0)[0], width);

				if (!queuedForUpload) {
					queuedForUpload = true;
					this.pattern = pattern;
					TextureUploader.INSTANCE.scheduleTextureUpload(this::upload);
				}
			}

			private void upload() {
				queuedForUpload = false;

				clearFramesTextureData();
				int[][] mipmaps = new int[this.mipmapLevels + 1][];
				mipmaps[0] = bitmap.apply(pattern);
				mipmaps = TextureUtil.generateMipmapData(this.mipmapLevels, this.width, mipmaps);
				framesTextureData.add(mipmaps);

				TextureUtil.uploadTextureMipmap(mipmaps, this.width, this.height, this.originX, this.originY, false, false);
			}
		}

		private final int size;

		private final ResourceLocation background;

		private TextureAtlasSprite backgroundSprite;

		private final Deque<PoolTexture> freeLocations = Queues.newArrayDeque();

		private final Map<IStencilPattern, PoolTexture> usedLocations = Maps.newHashMap();

		private StencilableBitmap bitmap;

		public TexturePool(int size, ResourceLocation background) {
			this.size = size;
			this.background = background;
		}

		public void allocate(AtlasTexture textureMap) {
			final int mipmapLevels = textureMap.getMipmapLevels();

			freeLocations.clear();
			for (int i = 0; i < size; i++) {
				final ResourceLocation newLocation = new ResourceLocation(background.getResourceDomain(), background.getResourcePath() + "-" + i);
				final PoolTexture pooledSprite = new PoolTexture(newLocation, mipmapLevels);
				freeLocations.push(pooledSprite);
				textureMap.setTextureEntry(pooledSprite);
			}

			usedLocations.clear();
		}

		public ResourceLocation get(IStencilPattern pattern) {
			PoolTexture result = usedLocations.get(pattern);
			if (result != null)
				return result.selfLocation;

			result = freeLocations.poll();
			if (result == null) {
				Log.warn("No more textures in pool for %s, returning blank one", background);
				return background;
			}

			result.loadPattern(pattern);
			usedLocations.put(pattern, result);
			return result.selfLocation;
		}

		public ResourceLocation getEmpty() {
			return background;
		}
	}

	private final Map<ResourceLocation, TexturePool> pools = Maps.newHashMap();

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre evt) {
		for (TexturePool pool : pools.values())
			pool.allocate(evt.getMap());
	}

	public StencilTextureManager register(ResourceLocation background, int size) {
		final TexturePool newPool = new TexturePool(size, background);
		final TexturePool prev = pools.put(background, newPool);
		Preconditions.checkState(prev == null, "Duplicate value for entry '%s'", background);
		return this;
	}

	public ResourceLocation getStencilTextureLocation(ResourceLocation background, IStencilPattern pattern) {
		final TexturePool texturePool = pools.get(background);
		Preconditions.checkState(texturePool != null, "Pool for '%' not registered");
		return texturePool.get(pattern);
	}

	public ResourceLocation getEmptyStencilTextureLocation(ResourceLocation background) {
		final TexturePool texturePool = pools.get(background);
		Preconditions.checkState(texturePool != null, "Pool for '%' not registered");
		return texturePool.getEmpty();
	}
}
