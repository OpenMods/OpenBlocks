package openblocks.client.renderer.block;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import openmods.utils.CollectionUtils;
import openmods.utils.render.RenderUtils;
import openmods.utils.render.RenderUtils.IQuadSink;
import org.apache.commons.lang3.tuple.Pair;

public class PathModel implements IRetexturableModel {

	public static final PathModel INSTANCE = new PathModel(ImmutableMap.<String, ResourceLocation> of());

	private final Map<String, ResourceLocation> textures;

	private PathModel(Map<String, ResourceLocation> textures) {
		this.textures = textures;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableSet.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return textures.values();
	}

	private static final Predicate<EnumFacing> ALL_SIDES = Predicates.alwaysTrue();

	private static final Predicate<EnumFacing> GENERAL = new Predicate<EnumFacing>() {
		@Override
		public boolean apply(EnumFacing input) {
			return input != EnumFacing.DOWN;
		}
	};

	private static final Predicate<EnumFacing> ONLY_BOTTOM = new Predicate<EnumFacing>() {
		@Override
		public boolean apply(EnumFacing input) {
			return input == EnumFacing.DOWN;
		}
	};

	private class Baked implements IPerspectiveAwareModel {

		private static final long NOT_MAGIC_NUMBER = 0xB98A60C6FAADD25EL;

		private final TextureAtlasSprite particle;

		private final List<TextureAtlasSprite> textures;

		private final VertexFormat format;

		private final Map<TransformType, TRSRTransformation> transforms;

		private final List<BakedQuad> itemQuads;

		private final Random rand = new Random();

		public Baked(TextureAtlasSprite particle, List<TextureAtlasSprite> textures, VertexFormat format, Map<TransformType, TRSRTransformation> transforms) {
			this.particle = particle;
			this.textures = ImmutableList.copyOf(textures);
			this.format = format;
			this.transforms = transforms;

			this.itemQuads = ImmutableList.copyOf(createQuads(NOT_MAGIC_NUMBER, ALL_SIDES));
		}

		private List<BakedQuad> createQuads(long seed, Predicate<EnumFacing> shouldInclude) {
			rand.setSeed(seed);

			final List<AxisAlignedBB> boundingBoxes = Lists.newArrayList();
			LOOP: for (int i = 0; i < 10; i++) {
				double width = rand.nextDouble() * 0.3 + 0.1;
				double length = rand.nextDouble() * 0.3 + 0.1;
				double pX = rand.nextDouble() * (1.0 - width);
				double pZ = rand.nextDouble() * (1.0 - length);

				AxisAlignedBB bb = new AxisAlignedBB(pX, 0, pZ, pX + width, 1.0 / 16.0, pZ + length);

				for (AxisAlignedBB box : boundingBoxes)
					if (box.intersectsWith(bb))
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

		private void addBox(final List<BakedQuad> output, AxisAlignedBB aabb, final TextureAtlasSprite texture, final Predicate<EnumFacing> shouldInclude) {

			RenderUtils.renderCube(new IQuadSink() {
				private UnpackedBakedQuad.Builder currentQuad = null;

				@Override
				public void addVertex(EnumFacing side, int vertex, double x, double y, double z) {
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

				private double selectV(EnumFacing side, double x, double y, double z) {
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

				private double selectU(EnumFacing side, double x, double y, double z) {
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
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if (state == null && rand == 0) return itemQuads;

			if (side == null)
				return createQuads(rand, GENERAL);

			if (side == EnumFacing.DOWN)
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
			final TRSRTransformation transform = transforms.get(cameraTransformType);
			Matrix4f mat = null;
			if (transform != null && !transform.equals(TRSRTransformation.identity())) mat = TRSRTransformation.blockCornerToCenter(transform).getMatrix();
			return Pair.of(this, mat);
		}

	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		final Map<TransformType, TRSRTransformation> transforms = MapWrapper.getTransforms(state);

		List<TextureAtlasSprite> textures = Lists.newArrayList();
		Optional<TextureAtlasSprite> maybeParticle = Optional.absent();

		for (Map.Entry<String, ResourceLocation> e : this.textures.entrySet()) {
			final TextureAtlasSprite tex = bakedTextureGetter.apply(e.getValue());
			if (e.getKey().equals("particle")) {
				maybeParticle = Optional.of(tex);
			} else {
				textures.add(tex);
			}
		}

		final TextureAtlasSprite missing = bakedTextureGetter.apply(TextureMap.LOCATION_MISSING_TEXTURE);
		final TextureAtlasSprite particle = maybeParticle.or(missing);

		if (textures.isEmpty()) textures.add(missing);

		return new Baked(particle, textures, format, transforms);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	@Override
	public IModel retexture(ImmutableMap<String, String> textures) {
		if (textures.isEmpty()) return this;

		final Map<String, ResourceLocation> newTextures = Maps.newHashMap();

		for (Map.Entry<String, String> e : textures.entrySet()) {
			final String location = e.getValue();
			if (Strings.isNullOrEmpty(location)) {
				newTextures.remove(e.getKey());
			} else {
				newTextures.put(e.getKey(), new ResourceLocation(location));
			}
		}

		return new PathModel(ImmutableMap.copyOf(newTextures));
	}

}
