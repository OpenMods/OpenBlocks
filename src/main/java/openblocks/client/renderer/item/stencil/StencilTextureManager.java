package openblocks.client.renderer.item.stencil;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.io.InputStream;
import java.util.Deque;
import java.util.Map;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.client.renderer.TextureUploader;
import openblocks.client.renderer.TextureUploader.IUploadableTexture;
import openblocks.common.IStencilPattern;
import openmods.Log;
import openmods.utils.io.BufferedResourceWrapper;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;

public class StencilTextureManager {

	private static class PoolPrimerTexture extends TextureAtlasSprite {

		private final ResourceLocation resourceLocation;

		private final int mipmapLevels;

		private boolean isLoaded;

		private boolean isMipmapped;

		private final ResourceLocation selfLocation;

		private StencilableBitmap bitmap;

		private PoolPrimerTexture(int mipmapLevels, ResourceLocation selfLocation, ResourceLocation resourceLocation) {
			super(selfLocation.toString());
			this.selfLocation = selfLocation;
			this.mipmapLevels = mipmapLevels;
			this.resourceLocation = resourceLocation;
		}

		@Override
		public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
			return true;
		}

		@Override
		public boolean load(IResourceManager manager, ResourceLocation location) {
			load(manager);
			return false;
		}

		public void load(IResourceManager manager) {
			if (!isLoaded) {
				IResource backgroundTexture = null;

				try {
					// streams from folder packs are already buffered, but zip ones aren't
					backgroundTexture = new BufferedResourceWrapper(manager.getResource(resourceLocation));
					final InputStream is = backgroundTexture.getInputStream();
					is.mark(0);
					{
						final PngSizeInfo sizeInfo = new PngSizeInfo(new CloseShieldInputStream(is));
						super.loadSprite(sizeInfo, false);
					}
					is.reset();

					super.loadSpriteFrames(backgroundTexture, mipmapLevels + 1);
				} catch (Exception e) {
					Log.warn(e, "Failed to load stencil base texture: %s", resourceLocation);
					FMLClientHandler.instance().trackMissingTexture(resourceLocation);
					width = height = 16;
					final int[][] mipmaps = new int[this.mipmapLevels + 1][];
					mipmaps[0] = TextureUtil.MISSING_TEXTURE_DATA;
					clearFramesTextureData();
					framesTextureData.add(mipmaps);

				} finally {
					IOUtils.closeQuietly(backgroundTexture);
				}

				bitmap = new StencilableBitmap(framesTextureData.get(0)[0], this.width);
				isLoaded = true;
			}
		}

		@Override
		public void generateMipmaps(int level) {
			Preconditions.checkArgument(level == this.mipmapLevels, "Mismatched mipmap levels: %s -> %s", level, this.mipmapLevels);
			if (!isMipmapped) {
				super.generateMipmaps(level);
				isMipmapped = true;
			}
		}

	}

	private static class PoolTexture extends TextureAtlasSprite implements IUploadableTexture {

		private final ResourceLocation selfLocation;

		private final PoolPrimerTexture primer;

		private final int mipmapLevels;

		private boolean requiresUpload;

		private PoolTexture(ResourceLocation selfLocation, PoolPrimerTexture primer, int mipmapLevels) {
			super(selfLocation.toString());
			this.selfLocation = selfLocation;
			this.primer = primer;
			this.mipmapLevels = mipmapLevels;
		}

		@Override
		public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
			return true;
		}

		private void copyTextureDataFromPrimer() {
			clearFramesTextureData();
			framesTextureData.add(primer.getFrameTextureData(0));
		}

		@Override
		public boolean load(IResourceManager manager, ResourceLocation location) {
			if (!primer.isLoaded)
				primer.load(manager);

			width = primer.getIconWidth();
			height = primer.getIconHeight();

			copyTextureDataFromPrimer();

			return false;
		}

		@Override
		public void generateMipmaps(int level) {
			Preconditions.checkArgument(level == this.mipmapLevels, "Mismatched mipmap levels: %s -> %s", level, this.mipmapLevels);
			if (!primer.isMipmapped)
				primer.generateMipmaps(level);

			copyTextureDataFromPrimer();
		}

		public void loadPattern(IStencilPattern pattern) {
			clearFramesTextureData();
			final int[][] mipmaps = new int[this.mipmapLevels + 1][];
			mipmaps[0] = primer.bitmap.apply(pattern);
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

	public static final StencilTextureManager INSTANCE = new StencilTextureManager();

	private StencilTextureManager() {}

	private static class TexturePool {
		private final int size;

		private final ResourceLocation background;

		private PoolPrimerTexture primerTexture;

		private final Deque<PoolTexture> freeLocations = Queues.newArrayDeque();

		private final Map<IStencilPattern, PoolTexture> usedLocations = Maps.newHashMap();

		public TexturePool(int size, ResourceLocation background) {
			this.size = size;
			this.background = background;
		}

		private static ResourceLocation getResourceLocation(TextureMap map, ResourceLocation location) {
			return new ResourceLocation(location.getResourceDomain(), String.format("%s/%s.png", map.getBasePath(), location.getResourcePath()));
		}

		public void allocate(TextureMap textureMap) {
			final int mipmapLevels = textureMap.getMipmapLevels();
			final ResourceLocation backgroundFullLocation = getResourceLocation(textureMap, background);
			final ResourceLocation primerLocation = new ResourceLocation(background.getResourceDomain(), background.getResourcePath() + "-primer");
			primerTexture = new PoolPrimerTexture(mipmapLevels, primerLocation, backgroundFullLocation);
			textureMap.setTextureEntry(primerTexture);

			freeLocations.clear();
			for (int i = 0; i < size; i++) {
				final ResourceLocation newLocation = new ResourceLocation(background.getResourceDomain(), background.getResourcePath() + "-" + i);
				final PoolTexture pooledSprite = new PoolTexture(newLocation, primerTexture, mipmapLevels);
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
				return primerTexture.selfLocation;
			}

			result.loadPattern(pattern);
			usedLocations.put(pattern, result);
			return result.selfLocation;
		}

		public ResourceLocation getPrimer() {
			return primerTexture.selfLocation;
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
		return texturePool.getPrimer();
	}
}
