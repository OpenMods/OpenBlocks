package openblocks.client.renderer.item.devnull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.vecmath.Matrix4f;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import openblocks.client.renderer.item.devnull.DevNullItemOverride.BakedModelParams;
import openmods.utils.CollectionUtils;

public class DevNullModel implements IModel {

	private static class ModelParams {
		public final Optional<ResourceLocation> normal;
		public final Optional<ResourceLocation> overflow;
		public final float scaleFactor;

		public ModelParams() {
			this(Optional.empty(), Optional.empty(), 0.875f);
		}

		public ModelParams(Optional<ResourceLocation> normal, Optional<ResourceLocation> overflow, float scale) {
			this.normal = normal;
			this.overflow = overflow;
			this.scaleFactor = scale;
		}

		public Set<ResourceLocation> getDependencies() {
			return Sets.union(CollectionUtils.asSet(normal), CollectionUtils.asSet(overflow));
		}

		public ModelParams merge(JsonObject obj) {
			Optional<ResourceLocation> newNormal = this.normal;
			Optional<ResourceLocation> newOverflow = this.overflow;
			float newScale = this.scaleFactor;
			boolean changed = false;

			if (obj.has("normal")) {
				final String v = JSONUtils.getString(obj, "normal");
				newNormal = Optional.of(new ModelResourceLocation(v));
				changed = true;
			}

			if (obj.has("overflow")) {
				final String v = JSONUtils.getString(obj, "overflow");
				newOverflow = Optional.of(new ModelResourceLocation(v));
				changed = true;
			}

			if (obj.has("scale")) {
				newScale = JSONUtils.getFloat(obj, "scale");
				changed = true;
			}

			return changed? new ModelParams(newNormal, newOverflow, newScale) : this;
		}

		public BakedModelParams bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
			final IModel modelNormal = getModel(normal);
			final IBakedModel bakedNormal = modelNormal.bake(modelNormal.getDefaultState(), format, bakedTextureGetter);

			final IModel modelOveflow = getModel(overflow);
			final IBakedModel bakedOverflow = modelOveflow.bake(modelOveflow.getDefaultState(), format, bakedTextureGetter);

			final Map<TransformType, Matrix4f> transforms = Maps.newHashMap();
			for (TransformType tt : TransformType.values())
				transforms.put(tt, bakedNormal.handlePerspective(tt).getRight());

			return new BakedModelParams(transforms, bakedNormal, bakedOverflow, scaleFactor);
		}

		private static IModel getModel(Optional<ResourceLocation> location) {
			if (location.isPresent()) {
				return ModelLoaderRegistry.getModelOrLogError(location.get(), "Couldn't load dependency: " + location.get());
			} else {
				return ModelLoaderRegistry.getMissingModel();
			}
		}
	}

	public static final DevNullModel INSTANCE = new DevNullModel();

	private final ModelParams gui;
	private final ModelParams world;

	private final Optional<ResourceLocation> particle;

	private final Optional<ResourceLocation> font;

	public DevNullModel() {
		this(new ModelParams(), new ModelParams(), Optional.empty(), Optional.empty());
	}

	public DevNullModel(ModelParams gui, ModelParams world, Optional<ResourceLocation> particle, Optional<ResourceLocation> font) {
		this.gui = gui;
		this.world = world;
		this.particle = particle;
		this.font = font;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Sets.union(gui.getDependencies(), world.getDependencies());
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return Sets.union(CollectionUtils.asSet(particle), CollectionUtils.asSet(font));
	}

	private static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "font/ascii");

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		final TextureAtlasSprite particle = bakedTextureGetter.apply(this.particle.orElse(AtlasTexture.LOCATION_MISSING_TEXTURE));
		final TextureAtlasSprite font = bakedTextureGetter.apply(this.font.orElse(DEFAULT_FONT));

		final BakedModelParams bakedGui = this.gui.bake(state, format, bakedTextureGetter);
		final BakedModelParams bakedWorld = this.world.bake(state, format, bakedTextureGetter);

		final DevNullItemOverride override = new DevNullItemOverride(bakedGui, bakedWorld, particle, font, format);
		return override.getEmptyBakedModel();
	}

	@Override
	public IModel retexture(ImmutableMap<String, String> textures) {
		final Optional<ResourceLocation> newParticle = tryReplace(textures.get("particle"), this.particle);
		final Optional<ResourceLocation> newFont = tryReplace(textures.get("font"), this.font);
		return new DevNullModel(this.gui, this.world, newParticle, newFont);
	}

	private static Optional<ResourceLocation> tryReplace(String newTexture, Optional<ResourceLocation> currentTexture) {
		if (newTexture == null)
			return currentTexture;

		if (newTexture.isEmpty())
			return Optional.empty();

		return Optional.of(new ResourceLocation(newTexture));
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		ModelParams newGui = this.gui;
		ModelParams newWorld = this.world;
		boolean changed = false;

		{
			final String data = customData.get("world");
			if (data != null) {
				final JsonElement json = new JsonParser().parse(data);
				newWorld = newWorld.merge(json.getAsJsonObject());
				changed = true;
			}
		}

		{
			final String data = customData.get("gui");
			if (data != null) {
				final JsonElement json = new JsonParser().parse(data);
				newGui = newGui.merge(json.getAsJsonObject());
				changed = true;
			}
		}

		return changed? new DevNullModel(newGui, newWorld, this.particle, this.font) : this;
	}

}
