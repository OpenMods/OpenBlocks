package openblocks.client.renderer.block.canvas;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import openblocks.client.renderer.block.canvas.CanvasSideState.OrientedTexture;
import openblocks.client.renderer.block.canvas.RenderLayerCache.LayerRenderInfo;
import openmods.geometry.FaceClassifier;

public class StencilModelTransformer {

	private static final int NO_TINT = -1;

	private static final double COVER_DELTA = 0.01;

	private static class Key {
		public final Optional<BlockState> innerBlockState;
		public final Optional<CanvasState> canvasState;
		public final BlockRenderLayer renderLayer;

		private final int hash;

		public Key(Optional<BlockState> innerBlockState, Optional<CanvasState> canvasState, BlockRenderLayer renderLayer) {
			this.innerBlockState = innerBlockState;
			this.canvasState = canvasState;
			this.renderLayer = renderLayer;
			this.hash = hash();
		}

		public int hash() {
			final int prime = 31;
			int result = 1;
			result = prime * result + canvasState.hashCode();
			result = prime * result + innerBlockState.hashCode();
			result = prime * result + renderLayer.hashCode();
			return result;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;

			if (obj instanceof Key) {
				final Key other = (Key)obj;
				return canvasState.equals(other.canvasState) &&
						innerBlockState.equals(other.innerBlockState) &&
						renderLayer == other.renderLayer;
			}
			return false;
		}

		@Override
		public String toString() {
			return "inner = " + innerBlockState + "\n" +
					"canvas = " + canvasState + "\n" +
					"layer = " + renderLayer;
		}
	}

	private final InnerModelInfo baseModel;

	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	private final VertexFormat vertexFormat;

	private final RenderLayerCache renderLayerCache;

	public StencilModelTransformer(IBakedModel baseModel, final Set<BlockRenderLayer> baseModelLayers, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, VertexFormat vertexFormat) {
		this.renderLayerCache = new RenderLayerCache(baseModelLayers::contains);
		this.baseModel = InnerModelInfo.create(null, baseModel, baseModelLayers::contains);
		this.bakedTextureGetter = bakedTextureGetter;
		this.vertexFormat = vertexFormat;
	}

	private final LoadingCache<BlockState, InnerModelInfo> innerModelCache = CacheBuilder.newBuilder().build(new CacheLoader<BlockState, InnerModelInfo>() {
		@Override
		public InnerModelInfo load(final BlockState blockState) {
			if (blockState.getRenderType() != BlockRenderType.MODEL)
				return baseModel;

			final IBakedModel innerModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(blockState);

			final Block block = blockState.getBlock();

			return InnerModelInfo.create(blockState, innerModel, input -> block.canRenderInLayer(blockState, input));
		}
	});

	private final LoadingCache<Key, ModelQuads> cache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES)
			.removalListener((RemovalNotification<Key, ModelQuads> notification) -> {
				notification.getKey().canvasState.ifPresent(CanvasState::release);
			})
			.build(new CacheLoader<Key, ModelQuads>() {
				@Override
				public ModelQuads load(Key key) throws Exception {
					key.canvasState.ifPresent(CanvasState::acquire);

					final LayerRenderInfo layersToRender = renderLayerCache.get(key.innerBlockState, key.renderLayer);
					if (layersToRender.layers.isEmpty()) return ModelQuads.EMPTY;

					final InnerModelInfo innerModel = key.innerBlockState.isPresent()? innerModelCache.get(key.innerBlockState.get()) : baseModel;

					if (!key.canvasState.isPresent()) {
						// no stencils, early exit
						final ModelQuads.Builder builder = ModelQuads.builder();
						for (BlockRenderLayer layer : layersToRender.layers)
							builder.merge(innerModel.layers.get(layer));

						return builder.build();
					}

					final CanvasState canvasState = key.canvasState.get();

					final FaceClassifier faceClassifier = new FaceClassifier(canvasState.applicationOrder());

					final ModelQuads.Builder builder = ModelQuads.builder();

					for (BlockRenderLayer layer : layersToRender.layers) {
						final ModelQuads layerQuads = innerModel.layers.get(layer);
						for (Direction side : Direction.VALUES)
							builder.addSidedQuads(side, prepareQuads(layerQuads.get(side), canvasState.sideStates, faceClassifier));

						builder.addGeneralQuads(prepareQuads(layerQuads.get(null), canvasState.sideStates, faceClassifier));
					}

					if (layersToRender.renderCovers)
						builder.addGeneralQuads(addStencilCovers(innerModel.bounds.grow(COVER_DELTA), canvasState.sideStates));

					return builder.build();
				}
			});

	private List<BakedQuad> prepareQuads(List<BakedQuad> baseQuads, Map<Direction, CanvasSideState> sides, FaceClassifier faceClassifier) {
		final List<BakedQuad> result = Lists.newArrayListWithExpectedSize(baseQuads.size());
		for (BakedQuad input : baseQuads)
			result.addAll(prepareQuad(input, sides, faceClassifier));

		return result;
	}

	private List<BakedQuad> prepareQuad(BakedQuad input, Map<Direction, CanvasSideState> sides, FaceClassifier faceClassifier) {
		final Vector3f pos[] = new Vector3f[4];

		final VertexFormat format = input.getFormat();

		final int[] vertexData = input.getVertexData();
		final ByteBuffer buffer = ByteBuffer.allocate(vertexData.length * Ints.BYTES);
		buffer.asIntBuffer().put(vertexData);
		buffer.limit(vertexData.length * Ints.BYTES);

		final int vertexSize = format.getNextOffset();
		for (int i = 0; i < 4; i++) {
			buffer.position(vertexSize * i);
			final float x = buffer.getFloat();
			final float y = buffer.getFloat();
			final float z = buffer.getFloat();

			pos[i] = new Vector3f(x, y, z);
		}
		buffer.rewind();

		final Vector3f quadNormal = calculateNormal(pos);
		final Optional<Direction> face = faceClassifier.classify(quadNormal);
		if (!face.isPresent()) return ImmutableList.of(input); // not painted face - return unpainted quad

		final List<BakedQuad> quads = Lists.newArrayList();

		final CanvasSideState sideInfo = sides.get(face.get());

		if (!sideInfo.isFullCover())
			quads.add(input);

		if (sideInfo.hasStencils()) {
			final OrientedTexture layersTextureInfo = sideInfo.getLayersTexture();
			quads.add(retextureQuad(input, buffer, face.get(), bakedTextureGetter.apply(layersTextureInfo.location), layersTextureInfo.orientation, pos));
		}

		return quads;
	}

	private static BakedQuad retextureQuad(BakedQuad original, ByteBuffer contents, Direction side, TextureAtlasSprite texture, TextureOrientation orientation, Vector3f[] positions) {
		final VertexFormat format = original.getFormat();
		final int vertexSize = format.getNextOffset();
		final int firstTextureOffset = format.getUvOffsetById(0);
		final StencilTextureProjection projection = new StencilTextureProjection(side);
		for (int i = 0; i < 4; i++) {
			final Vector3f position = positions[i];

			{
				contents.position(i * vertexSize);
				contents.putFloat(position.x);
				contents.putFloat(position.y);
				contents.putFloat(position.z);
			}

			{
				final int shiftedI = orientation.shift(i);
				contents.position(shiftedI * vertexSize + firstTextureOffset);

				final Vector2f projectedUv = projection.project(position);
				final float nU = texture.getInterpolatedU(16 * projectedUv.x);
				contents.putFloat(nU);
				final float nV = texture.getInterpolatedV(16 * projectedUv.y);
				contents.putFloat(nV);
			}
		}

		final int outputSize = format.getIntegerSize() * 4;
		final int[] data = new int[outputSize];
		contents.position(0);
		contents.asIntBuffer().get(data);

		return new BakedQuad(data, NO_TINT, original.getFace(), texture, original.shouldApplyDiffuseLighting(), format);
	}

	private static Vector3f calculateNormal(Vector3f[] pos) {
		final Vector3f a = new Vector3f();
		a.sub(pos[0], pos[2]);

		final Vector3f b = new Vector3f();
		b.sub(pos[1], pos[3]);

		a.cross(a, b);
		a.normalize();
		return a;
	}

	private List<BakedQuad> addStencilCovers(AxisAlignedBB bounds, Map<Direction, CanvasSideState> sides) {
		final StencilCoverQuadBuilder builder = new StencilCoverQuadBuilder(bounds, vertexFormat, NO_TINT);

		for (Map.Entry<Direction, CanvasSideState> e : sides.entrySet()) {
			final CanvasSideState state = e.getValue();
			final Optional<OrientedTexture> maybeCoverTextureInfo = state.getCoverTexture();
			if (maybeCoverTextureInfo.isPresent()) {
				final OrientedTexture coverTextureInfo = maybeCoverTextureInfo.get();
				builder.add(e.getKey(), bakedTextureGetter.apply(coverTextureInfo.location), coverTextureInfo.orientation);
			}
		}

		return builder.build();
	}

	public ModelQuads getQuads(Optional<BlockState> innerBlock, Optional<CanvasState> canvasState, BlockRenderLayer renderLayer) {
		final Key key = new Key(innerBlock, canvasState, renderLayer);
		return cache.getUnchecked(key);
	}

}
