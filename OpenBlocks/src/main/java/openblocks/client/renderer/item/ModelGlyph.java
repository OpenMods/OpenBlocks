package openblocks.client.renderer.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemGlyph;
import openmods.model.BakedModelAdapter;
import openmods.model.ModelUpdater;
import openmods.utils.CollectionUtils;

public class ModelGlyph implements IModel {

	private static class ModelOverride extends ItemOverrideList {

		private final BakedModel[] charModels;

		public ModelOverride(BakedModel[] charModels) {
			super(Collections.emptyList());
			this.charModels = charModels;
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, LivingEntity entity) {
			final IBakedModel model = getModel(stack).getOriginalModel();
			return model.getOverrides().handleItemState(model, stack, world, entity);
		}

		private BakedModel getModel(ItemStack stack) {
			final int charIndex = ItemGlyph.getCharIndex(stack);
			if (charIndex >= 0 && charIndex < charModels.length) {
				final BakedModel result = charModels[charIndex];
				if (result != null) return result;
			}

			return charModels[ItemGlyph.DEFAULT_CHAR_INDEX];
		}
	}

	private static class BakedModel extends BakedModelAdapter {

		private final ItemOverrideList override;

		public BakedModel(ItemOverrideList override, IBakedModel base, ImmutableMap<TransformType, TRSRTransformation> cameraTransforms) {
			super(base, cameraTransforms);
			this.override = override;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return override;
		}

		public IBakedModel getOriginalModel() {
			return base;
		}
	}

	private static class ClippedFontSprite extends TextureAtlasSprite {

		private final TextureAtlasSprite parent;

		private final int row;
		private final int column;

		private final float minU;
		private final float maxU;
		private final float minV;
		private final float maxV;

		protected ClippedFontSprite(TextureAtlasSprite parent, int index) {
			super(OpenBlocks.location("dynamic_glyph_" + index).toString());

			this.parent = parent;

			this.width = parent.getIconWidth() / 16;
			this.height = parent.getIconHeight() / 16;

			this.column = index % 16;
			this.row = index / 16;

			this.originX = parent.getOriginX() + column * width;
			this.originY = parent.getOriginY() + row * height;

			final float du = parent.getMaxU() - parent.getMinU();
			final float dv = parent.getMaxV() - parent.getMinV();

			final float pu = column / 16.0f;
			final float pv = row / 16.0f;

			final float npu = (column + 1) / 16.0f;
			final float npv = (row + 1) / 16.0f;

			this.minU = parent.getMinU() + du * pu;
			this.minV = parent.getMinV() + dv * pv;

			this.maxU = parent.getMinU() + du * npu;
			this.maxV = parent.getMinV() + dv * npv;
		}

		@Override
		public int getFrameCount() {
			// data is populated on demand, so this needs to be hardcoded
			return 1;
		}

		@Override
		public int[][] getFrameTextureData(int index) {
			if (framesTextureData.isEmpty())
				populateTextureData();

			return super.getFrameTextureData(index);
		}

		private void populateTextureData() {
			final int[][] fullParentData = parent.getFrameTextureData(0);
			final int[] parentData = fullParentData[0];
			final int mipmapDepth = fullParentData.length;

			final int[] textureData = new int[width * height];

			final int parentWidth = parent.getIconWidth();
			int si = row * height * parentWidth + column * width;
			int di = 0;

			for (int y = 0; y < height; y++) {
				System.arraycopy(parentData, si, textureData, di, width);
				si += parentWidth;
				di += width;
			}

			final int[][] fullTextureData = new int[mipmapDepth][];
			fullTextureData[0] = textureData;
			setFramesTextureData(Lists.<int[][]> newArrayList(fullTextureData));

			generateMipmaps(mipmapDepth - 1);
		}

		@Override
		public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
			return true;
		}

		@Override
		public boolean hasAnimationMetadata() {
			return false;
		}

		@Override
		public float getMinU() {
			return minU;
		}

		@Override
		public float getMaxU() {
			return maxU;
		}

		@Override
		public float getMinV() {
			return minV;
		}

		@Override
		public float getMaxV() {
			return maxV;
		}

		@Override
		public float getInterpolatedU(double u) {
			float f = this.maxU - this.minU;
			return this.minU + f * (float)u / 16.0F;
		}

		@Override
		public float getUnInterpolatedU(float u) {
			float f = this.maxU - this.minU;
			return (u - this.minU) / f * 16.0F;
		}

		@Override
		public float getInterpolatedV(double v) {
			float f = this.maxV - this.minV;
			return this.minV + f * (float)v / 16.0F;
		}

		@Override
		public float getUnInterpolatedV(float v) {
			float f = this.maxV - this.minV;
			return (v - this.minV) / f * 16.0F;
		}

		@Override
		public void setIconWidth(int newWidth) {
			// ignore, fixed value
		}

		@Override
		public void setIconHeight(int newHeight) {
			// ignore, fixed value
		}
	}

	public final Optional<ResourceLocation> baseModel;

	public final Optional<ResourceLocation> fontTexture;

	private static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "font/ascii");

	private static final ResourceLocation DYNAMIC_GLYPH_TEXTURE = OpenBlocks.location("dynamic_glyph");

	public static final IModel INSTANCE = new ModelGlyph(Optional.empty(), Optional.empty());

	private ModelGlyph(Optional<ResourceLocation> baseModel, Optional<ResourceLocation> fontTexture) {
		this.baseModel = baseModel;
		this.fontTexture = fontTexture;
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return CollectionUtils.asSet(fontTexture);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		if (baseModel.isPresent()) {
			final ResourceLocation baseModelLocation = baseModel.get();
			IModel templateModel = ModelLoaderRegistry.getModelOrLogError(baseModelLocation, "Couldn't load dependency: " + baseModelLocation);

			final TextureAtlasSprite font = bakedTextureGetter.apply(fontTexture.orElse(DEFAULT_FONT));

			final BakedModel[] overrides = new BakedModel[ItemGlyph.ALMOST_ASCII.length];

			final ModelOverride override = new ModelOverride(overrides);

			final ModelStateComposition templateModelState = new ModelStateComposition(state, templateModel.getDefaultState());

			final ImmutableMap<TransformType, TRSRTransformation> transforms = PerspectiveMapWrapper.getTransforms(templateModelState);

			for (int i = 0; i < ItemGlyph.ALMOST_ASCII.length; i++) {
				final char ch = ItemGlyph.ALMOST_ASCII[i];
				if (ItemGlyph.isHiddenCharacter(ch)) continue;

				final TextureAtlasSprite glyphIcon = new ClippedFontSprite(font, i);

				final Function<ResourceLocation, TextureAtlasSprite> patchedTextureGetter =
						(location) -> DYNAMIC_GLYPH_TEXTURE.equals(location)? glyphIcon : bakedTextureGetter.apply(location);

				final IBakedModel bakedCharModel = templateModel.bake(templateModelState, format, patchedTextureGetter);
				overrides[i] = new BakedModel(override, bakedCharModel, transforms);
			}

			return overrides[ItemGlyph.DEFAULT_CHAR_INDEX];
		} else {
			final IModel missing = ModelLoaderRegistry.getMissingModel();
			return missing.bake(missing.getDefaultState(), format, bakedTextureGetter);
		}
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		final ModelUpdater updater = new ModelUpdater(customData);
		final Optional<ResourceLocation> baseModel = updater.get("base", ModelUpdater.MODEL_LOCATION, this.baseModel);
		return updater.hasChanged()? new ModelGlyph(baseModel, this.fontTexture) : this;
	}

	@Override
	public IModel retexture(ImmutableMap<String, String> textures) {
		final Optional<ResourceLocation> fontTexture = tryReplaceTexture(textures.get("font"), this.fontTexture);
		return fontTexture.equals(this.fontTexture)? this : new ModelGlyph(this.baseModel, this.fontTexture);
	}

	private static Optional<ResourceLocation> tryReplaceTexture(String newTexture, Optional<ResourceLocation> currentTexture) {
		if (newTexture == null)
			return currentTexture;

		if (newTexture.isEmpty())
			return Optional.empty();

		return Optional.of(new ResourceLocation(newTexture));
	}
}
