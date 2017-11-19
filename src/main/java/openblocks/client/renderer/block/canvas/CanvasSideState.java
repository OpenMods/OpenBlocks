package openblocks.client.renderer.block.canvas;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.client.renderer.item.stencil.StencilItemOverride;
import openblocks.client.renderer.item.stencil.StencilTextureManager;
import openblocks.common.IStencilPattern;
import org.apache.commons.lang3.tuple.Pair;

public class CanvasSideState {

	public static class OrientedTexture {
		public final ResourceLocation location;

		public final TextureOrientation orientation;

		public OrientedTexture(ResourceLocation location, TextureOrientation orientation) {
			this.location = location;
			this.orientation = orientation;
		}

	}

	private final int background;

	private final List<CanvasLayer> layers;

	private final TextureOrientation layersOrientation;

	@Nullable
	private OrientedTexture layersTexture;

	private final Optional<IStencilPattern> cover;

	private final TextureOrientation coverOrientation;

	@Nullable
	private Optional<OrientedTexture> coverTexture;

	private final int hash;

	private CanvasSideState(int background, List<CanvasLayer> layers, TextureOrientation layersOrientation, Optional<IStencilPattern> cover, TextureOrientation coverOrientation) {
		this.background = background;
		this.layers = ImmutableList.copyOf(layers);
		this.layersOrientation = layersOrientation;
		this.cover = cover;
		this.coverOrientation = coverOrientation;
		this.hash = hash();
	}

	private int hash() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Ints.hashCode(background);
		result = prime * result + cover.hashCode();
		result = prime * result + coverOrientation.hashCode();
		result = prime * result + layers.hashCode();
		result = prime * result + layersOrientation.hashCode();
		return result;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		if (obj instanceof CanvasSideState) {
			final CanvasSideState other = (CanvasSideState)obj;
			return this.background == other.background &&
					this.coverOrientation == other.coverOrientation &&
					this.cover.equals(other.cover) &&
					this.layersOrientation == other.layersOrientation &&
					this.layers.equals(other.layers);
		}
		return false;
	}

	public boolean isFullCover() {
		return background != 0;
	}

	public boolean hasStencils() {
		return isFullCover() || !layers.isEmpty();
	}

	@SideOnly(Side.CLIENT)
	public OrientedTexture getLayersTexture() {
		if (layersTexture == null) {
			final ResourceLocation textureLocation = CanvasTextureManager.INSTANCE.getTexture(background, layers);
			layersTexture = new OrientedTexture(textureLocation, layersOrientation);
		}

		return layersTexture;
	}

	@SideOnly(Side.CLIENT)
	public Optional<OrientedTexture> getCoverTexture() {
		if (coverTexture == null) {
			if (cover.isPresent()) {
				final IStencilPattern coverPattern = cover.get();
				final ResourceLocation textureLocation = StencilTextureManager.INSTANCE.getStencilTextureLocation(StencilItemOverride.BACKGROUND_TEXTURE, coverPattern);
				coverTexture = Optional.of(new OrientedTexture(textureLocation, coverOrientation));
			} else {
				coverTexture = Optional.empty();
			}
		}

		return coverTexture;
	}

	public void onRender() {
		// May not be called later due to caching
		getLayersTexture();
	}

	private int referenceCount = 0;

	public synchronized void acquire() {
		if (referenceCount <= 0) referenceCount = 1;
		else referenceCount++;
	}

	public synchronized void release() {
		if (--referenceCount <= 0) {
			if (layersTexture != null) {
				CanvasTextureManager.INSTANCE.releaseTexture(background, layers);
				layersTexture = null;
			}
		}
	}

	// huh, looks weird...
	private static final Map<CanvasSideState, CanvasSideState> canonicMap = Maps.newHashMap();

	private synchronized static CanvasSideState getCanonic(CanvasSideState i) {
		CanvasSideState o = canonicMap.get(i);
		if (o == null) {
			canonicMap.put(i, i);
			o = i;
		}

		return o;
	}

	static void onTextureReload() {
		for (CanvasSideState k : canonicMap.keySet())
			k.clearTextures();
	}

	private void clearTextures() {
		layersTexture = null;
		coverTexture = null;
	}

	public static class Builder {
		private int background;

		private final List<CanvasLayer> layers = Lists.newArrayList();

		public Builder addLayer(IStencilPattern pattern, int color, TextureOrientation orientation) {
			layers.add(new CanvasLayer(pattern, color, orientation));
			return this;
		}

		public Builder withBackground(int background) {
			this.background = background;
			return this;
		}

		public CanvasSideState withCover(IStencilPattern cover, TextureOrientation rotation) {
			final Pair<TextureOrientation, List<CanvasLayer>> layers = reorientLayers(this.layers);
			return getCanonic(new CanvasSideState(background, layers.getRight(), layers.getLeft(), Optional.of(cover), rotation));
		}

		public CanvasSideState withoutCover() {
			final Pair<TextureOrientation, List<CanvasLayer>> layers = reorientLayers(this.layers);
			return getCanonic(new CanvasSideState(background, layers.getRight(), layers.getLeft(), Optional.empty(), TextureOrientation.R0));
		}

		private static Pair<TextureOrientation, List<CanvasLayer>> reorientLayers(List<CanvasLayer> layers) {
			if (layers.isEmpty()) return Pair.of(TextureOrientation.R0, layers);

			final TextureOrientation baseRotation = layers.get(0).orientation;

			final List<CanvasLayer> rotatedLayers = Lists.newArrayListWithCapacity(layers.size());
			for (CanvasLayer originalLayer : layers) {
				final TextureOrientation newRotation = originalLayer.orientation.subtract(baseRotation);
				rotatedLayers.add(new CanvasLayer(originalLayer.pattern, originalLayer.color, newRotation));
			}

			return Pair.of(baseRotation, rotatedLayers);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return layers + (cover.isPresent()? ("+" + cover.toString()) : "");
	}
}
