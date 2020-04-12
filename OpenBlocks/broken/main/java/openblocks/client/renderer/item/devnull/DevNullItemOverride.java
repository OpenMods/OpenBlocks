package openblocks.client.renderer.item.devnull;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.SimpleModelFontRenderer;
import openblocks.Config;
import openblocks.common.item.ItemDevNull;
import org.apache.commons.lang3.tuple.Pair;

public class DevNullItemOverride extends ItemOverrideList {

	private class BakedDevNull implements IBakedModel {

		private final ModelKey key;

		private final List<BakedQuad> quads;

		private final boolean isAmbientOcclusion;

		private final boolean isGui3d;

		public BakedDevNull(ModelKey key, List<BakedQuad> quads, boolean isAmbientOcclusion, boolean isGui3d) {
			this.key = key;
			this.quads = quads;
			this.isAmbientOcclusion = isAmbientOcclusion;
			this.isGui3d = isGui3d;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, long rand) {
			return quads;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return isAmbientOcclusion;
		}

		@Override
		public boolean isGui3d() {
			return isGui3d;
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
			return DevNullItemOverride.this;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			final ModelKey updatedKey = key.update(cameraTransformType);
			return getModel(this, updatedKey);
		}

	}

	public static class BakedModelParams {
		public final IBakedModel normal;
		public final IBakedModel overflow;

		private final float scaleFactor;
		private final Map<TransformType, Matrix4f> transforms;

		public BakedModelParams(Map<TransformType, Matrix4f> transforms, IBakedModel normal, IBakedModel overflow, float scaleFactor) {
			this.transforms = transforms;
			this.normal = normal;
			this.overflow = overflow;
			this.scaleFactor = scaleFactor;
		}
	}

	private static final ResourceLocation dummyFontLocation = new ResourceLocation("minecraft", "textures/font/ascii.png");

	private class ModelFactory extends CacheLoader<ModelKey, Pair<? extends IBakedModel, Matrix4f>> {

		@Override
		public Pair<? extends IBakedModel, Matrix4f> load(ModelKey key) {
			final int depth = key.depth;
			IBakedModel innerModel = key.innerItem;
			// should be caught by earlier method
			Preconditions.checkNotNull(innerModel);
			final TransformType transformType = key.transform;

			final boolean isGui = key.transform == TransformType.GUI;

			final float scale = (float)Math.pow(isGui? scaleFactor2d : scaleFactor3d, depth + 1);

			final Matrix4f quadTransformMatrix = isGui? scale2d(scale) : scale3d(scale);

			final Pair<? extends IBakedModel, Matrix4f> tmp = innerModel.handlePerspective(key.transform);
			innerModel = tmp.getLeft();
			final Matrix4f modelTransformMatrix = tmp.getRight();

			final Matrix4f perspectiveMatrix;
			if (isGui) {
				// bake contained item transform into quads
				if (modelTransformMatrix != null)
					quadTransformMatrix.mul(modelTransformMatrix);

				// use container transform
				perspectiveMatrix = transforms2d.get(TransformType.GUI);
			} else {
				// quads (frame and contents) will be scaled, but contained item's original transform will be returned as perspective
				perspectiveMatrix = modelTransformMatrix;
			}

			wrapTransform(quadTransformMatrix);

			final List<BakedQuad> allQuads = Lists.newArrayList();
			apppendFrameQuads(isGui? frameQuads2d : frameQuads3d, depth, transformType, allQuads);
			appendScaledModelQuads(allQuads, innerModel, quadTransformMatrix, ItemDevNull.NESTED_ITEM_TINT_DELTA);

			if (isGui) {
				appendItemCount(allQuads, key.stackSize);
			}

			final BakedDevNull model = new BakedDevNull(key, allQuads, innerModel.isAmbientOcclusion(), innerModel.isGui3d());
			return Pair.of(model, perspectiveMatrix);
		}
	}

	static class ModelKey {
		public final int depth;
		@Nullable
		public final IBakedModel innerItem;
		public final int stackSize;
		public final TransformType transform;

		public ModelKey(@Nullable IBakedModel innerItem, int depth, int stackSize, TransformType transform) {
			this.innerItem = innerItem;
			this.depth = depth;
			this.stackSize = stackSize;
			this.transform = transform;
		}

		// worst case scenario: identity checks for innerItem. We are ok with that

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;

			if (obj instanceof ModelKey) {
				final ModelKey other = (ModelKey)obj;
				return this.depth == other.depth &&
						Objects.equal(this.innerItem, other.innerItem) &&
						this.transform == other.transform &&
						this.stackSize == other.stackSize;
			}

			return false;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + depth;
			result = prime * result + (innerItem != null? innerItem.hashCode() : 0);
			result = prime * result + transform.hashCode();
			result = prime * result + Integer.hashCode(stackSize);
			return result;
		}

		public ModelKey update(TransformType cameraTransformType) {
			return new ModelKey(this.innerItem, this.depth, this.stackSize, cameraTransformType);
		}
	}

	private static final Matrix4f CENTER_TO_CORNER = new Matrix4f();

	static {
		CENTER_TO_CORNER.setIdentity();
		CENTER_TO_CORNER.m03 = CENTER_TO_CORNER.m13 = CENTER_TO_CORNER.m23 = -0.5f;
	}

	private static final Matrix4f CORNER_TO_CENTER = new Matrix4f();

	static {
		CORNER_TO_CENTER.setIdentity();
		CORNER_TO_CENTER.m03 = CORNER_TO_CENTER.m13 = CORNER_TO_CENTER.m23 = +0.5f;
	}

	private static List<BakedQuad> appendScaledModelQuads(List<BakedQuad> output, IBakedModel model, Matrix4f transform, int tintDelta) {
		for (Direction side : Direction.VALUES)
			for (BakedQuad quad : model.getQuads(null, side, 0))
				output.add(rescaleQuad(quad, transform, tintDelta));

		for (BakedQuad quad : model.getQuads(null, null, 0))
			output.add(rescaleQuad(quad, transform, tintDelta));

		return output;
	}

	private void appendItemCount(List<BakedQuad> output, int count) {
		if (count > 1) {
			final String s = Integer.toString(count);
			fontRenderer.drawStringWithShadow(s, 19 - 2 - fontRenderer.getStringWidth(s), 6 + 3, Config.devNullCountColor);
			output.addAll(fontRenderer.build());
		}
	}

	private static IBakedModel getItemModel(@Nonnull ItemStack stack, World world, LivingEntity entity) {
		final IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
		return model.getOverrides().handleItemState(model, stack, world, entity);
	}

	private static BakedQuad rescaleQuad(BakedQuad quad, Matrix4f transform, int tintDelta) {
		final int delta = quad.getFormat().getIntegerSize();
		final int[] inputVertexData = quad.getVertexData();
		final int[] outputVertexData = Arrays.copyOf(inputVertexData, inputVertexData.length);
		int elementOffset = 0;
		for (int i = 0; i < 4; i++) {
			float x = Float.intBitsToFloat(inputVertexData[elementOffset + 0]);
			float y = Float.intBitsToFloat(inputVertexData[elementOffset + 1]);
			float z = Float.intBitsToFloat(inputVertexData[elementOffset + 2]);

			final Vector4f v = new Vector4f(x, y, z, 1);
			transform.transform(v);

			outputVertexData[elementOffset + 0] = Float.floatToIntBits(v.x);
			outputVertexData[elementOffset + 1] = Float.floatToIntBits(v.y);
			outputVertexData[elementOffset + 2] = Float.floatToIntBits(v.z);

			elementOffset += delta;
		}

		final int tintIndex = quad.hasTintIndex()? quad.getTintIndex() + tintDelta : -1;
		return new BakedQuad(outputVertexData, tintIndex, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
	}

	private static Matrix4f scale2d(float scale) {
		final Matrix4f result = new Matrix4f();
		result.m00 = result.m11 = scale;
		result.m22 = result.m33 = 1.0f;
		return result;
	}

	private static Matrix4f scale3d(float scale) {
		final Matrix4f result = new Matrix4f();
		result.m00 = result.m11 = result.m22 = scale;
		result.m33 = 1.0f;
		return result;
	}

	private static void wrapTransform(Matrix4f inner) {
		inner.mul(CENTER_TO_CORNER);
		inner.mul(CORNER_TO_CENTER, inner);
	}

	private final TextureAtlasSprite particle;

	private final float scaleFactor2d;
	private final List<List<BakedQuad>> frameQuads2d;
	private final List<IBakedModel> emptyFrameModels2d;
	private final Map<TransformType, Matrix4f> transforms2d;

	private final float scaleFactor3d;
	private final List<List<BakedQuad>> frameQuads3d;
	private final List<IBakedModel> emptyFrameModels3d;
	private final Map<TransformType, Matrix4f> transforms3d;

	private final SimpleModelFontRenderer fontRenderer;

	private final LoadingCache<ModelKey, Pair<? extends IBakedModel, Matrix4f>> wrappedModelCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS).build(new ModelFactory());

	private static List<BakedQuad> rescaleModel(IBakedModel model, float scale, boolean is3d) {
		final Matrix4f scaleMatrix = is3d? scale3d(scale) : scale2d(scale);
		wrapTransform(scaleMatrix);

		return appendScaledModelQuads(Lists.newArrayList(), model, scaleMatrix, 0);
	}

	private static List<List<BakedQuad>> prepareScaledFrames(BakedModelParams params, boolean is3d) {
		final List<List<BakedQuad>> scaledFrames = Lists.newArrayListWithCapacity(ItemDevNull.STACK_LIMIT + 1);
		float scale = 1;

		for (int i = 0; i < ItemDevNull.STACK_LIMIT; i++) {
			scaledFrames.add(rescaleModel(params.normal, scale, is3d));
			scale *= params.scaleFactor;
		}

		scaledFrames.add(rescaleModel(params.overflow, scale, is3d));

		return scaledFrames;
	}

	private static List<List<BakedQuad>> composeScaledFrames(List<List<BakedQuad>> scaledFrames) {
		final ImmutableList.Builder<List<BakedQuad>> frames = ImmutableList.builder();
		for (int i = 0; i <= ItemDevNull.STACK_LIMIT; i++) {
			final ImmutableList.Builder<BakedQuad> nestedFrames = ImmutableList.builder();
			for (int j = 0; j <= i; j++)
				nestedFrames.addAll(scaledFrames.get(j));

			frames.add(nestedFrames.build());
		}

		return frames.build();
	}

	private List<IBakedModel> prepareFramesBakedModels(List<List<BakedQuad>> scaledFrames, TransformType type) {
		final ImmutableList.Builder<IBakedModel> emptyFrameModels = ImmutableList.builder();

		for (int i = 0; i < scaledFrames.size(); i++) {
			final List<BakedQuad> quads = scaledFrames.get(i);
			final ModelKey key = new ModelKey(null, i, 1, type);
			emptyFrameModels.add(new BakedDevNull(key, quads, true, false));
		}

		return emptyFrameModels.build();
	}

	public DevNullItemOverride(BakedModelParams gui, BakedModelParams world, TextureAtlasSprite particle, TextureAtlasSprite font, VertexFormat format) {
		super(ImmutableList.of());

		this.particle = particle;

		{
			final List<List<BakedQuad>> scaledFrames = prepareScaledFrames(gui, false);
			this.frameQuads2d = composeScaledFrames(scaledFrames);
			this.emptyFrameModels2d = prepareFramesBakedModels(this.frameQuads2d, TransformType.GUI);
			this.scaleFactor2d = gui.scaleFactor;
			this.transforms2d = gui.transforms;
		}

		{
			final List<List<BakedQuad>> scaledFrames = prepareScaledFrames(world, true);
			this.frameQuads3d = composeScaledFrames(scaledFrames);
			this.emptyFrameModels3d = prepareFramesBakedModels(this.frameQuads3d, TransformType.NONE);
			this.scaleFactor3d = world.scaleFactor;
			this.transforms3d = world.transforms;
		}

		final Matrix4f textTranform = new Matrix4f();
		final float scale = 1f / 16f;
		textTranform.m00 = +scale;
		textTranform.m11 = -scale;
		textTranform.m22 = +scale;
		textTranform.m33 = 1;
		textTranform.setTranslation(new Vector3f(0, 1, 2f));

		fontRenderer = new SimpleModelFontRenderer(
				Minecraft.getMinecraft().gameSettings,
				dummyFontLocation,
				Minecraft.getMinecraft().getTextureManager(),
				false,
				textTranform,
				format) {

			@Override
			protected float renderUnicodeChar(char c, boolean italic) {
				return super.renderDefaultChar(126, italic);
			}
		};

		fontRenderer.setSprite(font);
	}

	private static void apppendFrameQuads(List<List<BakedQuad>> framesQuads, int depth, TransformType transformType, List<BakedQuad> output) {
		final List<BakedQuad> frameQuads = framesQuads.get(depth);
		if (transformType == TransformType.GUI) {
			for (BakedQuad q : frameQuads)
				if (q.getFace() == Direction.SOUTH)
					output.add(q);
		} else {
			output.addAll(frameQuads);
		}
	}

	public IBakedModel getEmptyBakedModel() {
		return emptyFrameModels2d.get(0);
	}

	@Override
	public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, LivingEntity entity) {
		final Pair<ItemStack, Integer> r = ItemDevNull.getContents(stack);
		final int depth = Math.max(1, r.getRight());

		if (depth >= ItemDevNull.STACK_LIMIT)
			return emptyFrameModels2d.get(ItemDevNull.STACK_LIMIT);

		final int modelId = depth - 1;
		final ItemStack innerStack = r.getLeft();
		if (innerStack.isEmpty())
			return emptyFrameModels2d.get(modelId);

		final IBakedModel innerModel = getItemModel(innerStack, world, entity);

		final ModelKey key = new ModelKey(innerModel, modelId, innerStack.getCount(), TransformType.NONE);
		return wrappedModelCache.getUnchecked(key).getLeft();
	}

	private Pair<IBakedModel, Matrix4f> selectEmptyModel(int depth, boolean is2d, TransformType transformType) {
		final Matrix4f transform = getTransform(is2d, transformType);
		final IBakedModel model = (is2d? emptyFrameModels2d : emptyFrameModels3d).get(depth);
		return Pair.of(model, transform);
	}

	private Matrix4f getTransform(boolean is2d, TransformType transformType) {
		return (is2d? transforms2d : transforms3d).get(transformType);
	}

	private Pair<? extends IBakedModel, Matrix4f> getModel(BakedDevNull current, ModelKey key) {
		boolean isGui = key.transform == TransformType.GUI;

		if (key.equals(current.key)) return Pair.of(current, getTransform(isGui, key.transform));

		if (key.depth >= ItemDevNull.STACK_LIMIT) return selectEmptyModel(ItemDevNull.STACK_LIMIT, isGui, key.transform);

		if (key.innerItem == null)
			return selectEmptyModel(key.depth, isGui, key.transform);

		return wrappedModelCache.getUnchecked(key);
	}
}
