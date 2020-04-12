package openblocks.client.renderer.block;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.vecmath.Matrix4f;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import openmods.model.ModelTextureMap;
import openmods.model.ModelUpdater;
import openmods.utils.CollectionUtils;
import openmods.utils.render.RenderUtils;
import openmods.utils.render.RenderUtils.IQuadSink;
import org.apache.commons.lang3.tuple.Pair;

public class PathModel implements IModel {

	private static final int DEFAULT_MAX_BLOCK_COUNT = 10;

	private static final long NOT_MAGIC_NUMBER = 0;

	public static final PathModel INSTANCE = new PathModel(new ModelTextureMap(), DEFAULT_MAX_BLOCK_COUNT, NOT_MAGIC_NUMBER, Optional.empty());

	private final ModelTextureMap textures;

	private final int maxBlockCount;

	private final long inventorySeed;

	private final Optional<ResourceLocation> inventoryTransformProvider;

	public PathModel(ModelTextureMap textures, int maxBlockCount, long inventorySeed, Optional<ResourceLocation> inventoryTransformProvider) {
		this.textures = textures;
		this.maxBlockCount = maxBlockCount;
		this.inventorySeed = inventorySeed;
		this.inventoryTransformProvider = inventoryTransformProvider;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return CollectionUtils.asSet(inventoryTransformProvider);
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return textures.getTextures();
	}

	private static final Predicate<Direction> ALL_SIDES = Predicates.alwaysTrue();

	private static final Predicate<Direction> GENERAL = input -> input != Direction.DOWN;

	private static final Predicate<Direction> ONLY_BOTTOM = input -> input == Direction.DOWN;

	private class Baked implements IBakedModel {

		private final TextureAtlasSprite particle;

		private final List<TextureAtlasSprite> textures;

		private final VertexFormat format;

		private final Map<TransformType, Matrix4f> transforms;

		private final List<BakedQuad> itemQuads;

		private final Random rand = new Random();

		public Baked(TextureAtlasSprite particle, List<TextureAtlasSprite> textures, VertexFormat format, Map<TransformType, Matrix4f> transforms) {
			this.particle = particle;
			this.textures = ImmutableList.copyOf(textures);
			this.format = format;
			this.transforms = transforms;

			this.itemQuads = ImmutableList.copyOf(createQuads(inventorySeed, ALL_SIDES));
		}

		private List<BakedQuad> createQuads(long seed, Predicate<Direction> shouldInclude) {
			rand.setSeed(seed);

			final List<AxisAlignedBB> boundingBoxes = Lists.newArrayList();
			LOOP:
			for (int i = 0; i < maxBlockCount; i++) {
				double width = rand.nextDouble() * 0.3 + 0.1;
				double length = rand.nextDouble() * 0.3 + 0.1;
				double pX = rand.nextDouble() * (1.0 - width);
				double pZ = rand.nextDouble() * (1.0 - length);

				AxisAlignedBB bb = new AxisAlignedBB(pX, 0, pZ, pX + width, 1.0 / 16.0, pZ + length);

				for (AxisAlignedBB box : boundingBoxes)
					if (box.intersects(bb))
						break LOOP;

				boundingBoxes.add(bb);
			}

			final List<BakedQuad> result = Lists.newArrayListWithCapacity(boundingBoxes.size() * 6);

			for (AxisAlignedBB aabb : boundingBoxes) {
				final TextureAtlasSprite tex = CollectionUtils.getRandom(textures, rand);
				addBox(result, aabb, tex, shouldInclude);
			}

			return result;
		}

		private void addBox(final List<BakedQuad> output, AxisAlignedBB aabb, final TextureAtlasSprite texture, final Predicate<Direction> shouldInclude) {

			RenderUtils.renderCube(new IQuadSink() {
				private UnpackedBakedQuad.Builder currentQuad = null;

				@Override
				public void addVertex(Direction side, int vertex, double x, double y, double z) {
					if (!shouldInclude.apply(side)) return;

					if (currentQuad == null) {
						currentQuad = new UnpackedBakedQuad.Builder(format);
						currentQuad.setApplyDiffuseLighting(true);
						currentQuad.setQuadTint(0);
						currentQuad.setQuadOrientation(side);
						currentQuad.setTexture(texture);
					}

					for (int i = 0; i < format.getElementCount(); i++) {
						final VertexFormatElement e = format.getElement(i);
						switch (e.getUsage()) {
							case POSITION:
								currentQuad.put(i, (float)x, (float)y, (float)z);
								break;
							case COLOR:
								currentQuad.put(i, 1.0f, 1.0f, 1.0f, 1.0f);
								break;
							case NORMAL:
								currentQuad.put(i, side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
								break;
							case UV:
								if (e.getIndex() == 0) {
									currentQuad.put(i,
											texture.getInterpolatedU(16 * selectU(side, x, y, z)),
											texture.getInterpolatedV(16 * selectV(side, x, y, z)));
								} else {
									currentQuad.put(i, 0, 0);
								}
								break;
							default:
								currentQuad.put(i);
								break;
						}
					}

					if (vertex == 3) {
						output.add(currentQuad.build());
						currentQuad = null;
					}
				}

				private double selectV(Direction side, double x, double y, double z) {
					switch (side) {
						case UP:
						case DOWN:
							return z;
						case NORTH:
							return 1 - x;
						case SOUTH:
							return x;
						case EAST:
							return 1 - z;
						case WEST:
							return z;
						default:
							throw new AssertionError(side);
					}
				}

				private double selectU(Direction side, double x, double y, double z) {
					switch (side) {
						case NORTH:
						case SOUTH:
						case EAST:
						case WEST:
							return y;
						case UP:
						case DOWN:
							return x;
						default:
							throw new AssertionError(side);
					}
				}
			}, aabb);

		}

		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, long rand) {
			if (state == null && rand == 0) return itemQuads;

			if (side == null)
				return createQuads(rand, GENERAL);

			if (side == Direction.DOWN)
				return createQuads(rand, ONLY_BOTTOM);

			return ImmutableList.of();
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return particle;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.NONE;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			final Matrix4f transform = transforms.get(cameraTransformType);
			return Pair.of(this, transform);
		}

	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		final Map<TransformType, Matrix4f> transforms = extractInventoryTransforms(state, format, bakedTextureGetter);

		List<TextureAtlasSprite> textures = Lists.newArrayList();
		Optional<TextureAtlasSprite> maybeParticle = Optional.empty();

		for (Map.Entry<String, TextureAtlasSprite> e : this.textures.bakeWithKeys(bakedTextureGetter).entrySet()) {
			if (e.getKey().equals("particle")) {
				maybeParticle = Optional.of(e.getValue());
			} else {
				textures.add(e.getValue());
			}
		}

		final TextureAtlasSprite missing = bakedTextureGetter.apply(AtlasTexture.LOCATION_MISSING_TEXTURE);
		final TextureAtlasSprite particle = maybeParticle.orElse(missing);

		if (textures.isEmpty()) textures.add(missing);

		return new Baked(particle, textures, format, transforms);
	}

	private Map<TransformType, Matrix4f> extractInventoryTransforms(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		if (inventoryTransformProvider.isPresent()) {
			final IModel model = ModelLoaderRegistry.getModelOrLogError(inventoryTransformProvider.get(), "Couldn't load MultiLayerModel dependency: " + inventoryTransformProvider.get());
			final IBakedModel bakedModel = model.bake(model.getDefaultState(), format, bakedTextureGetter);
			return extractInventoryTransformsFromModel(bakedModel);
		}
		// fallback: get transforms from our own model
		return extractInventoryTransformsFromState(state);
	}

	private static Map<TransformType, Matrix4f> extractInventoryTransformsFromModel(IBakedModel model) {
		final Map<TransformType, Matrix4f> output = Maps.newHashMap();
		for (TransformType type : TransformType.values()) {
			final Pair<? extends IBakedModel, Matrix4f> transform = model.handlePerspective(type);
			output.put(type, transform.getRight());
		}
		return output;
	}

	private static Map<TransformType, Matrix4f> extractInventoryTransformsFromState(IModelState state) {
		final Map<TransformType, Matrix4f> output = Maps.newHashMap();
		for (TransformType type : TransformType.values()) {
			Matrix4f mat = null;
			final Optional<TRSRTransformation> maybeTransform = state.apply(Optional.of(type));
			if (maybeTransform.isPresent()) {
				final TRSRTransformation transform = maybeTransform.get();
				if (!transform.equals(TRSRTransformation.identity())) {
					mat = TRSRTransformation.blockCornerToCenter(transform).getMatrix();
				}
			}

			output.put(type, mat);
		}
		return output;
	}

	@Override
	public IModel retexture(ImmutableMap<String, String> updates) {
		final Optional<ModelTextureMap> newTextures = textures.update(updates);
		return newTextures.isPresent()? new PathModel(newTextures.get(), maxBlockCount, inventorySeed, inventoryTransformProvider) : this;
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		final ModelUpdater updater = new ModelUpdater(customData);
		final int maxBlockCount = updater.get("maxBlockCount", ModelUpdater.TO_INT, this.maxBlockCount);
		final long inventorySeed = updater.get("inventorySeed", ModelUpdater.TO_LONG, this.inventorySeed);
		final Optional<ResourceLocation> inventoryTransformProvider = updater.get("inventoryTransform", ModelUpdater.MODEL_LOCATION, this.inventoryTransformProvider);

		return updater.hasChanged()? new PathModel(this.textures, maxBlockCount, inventorySeed, inventoryTransformProvider) : this;
	}

}
