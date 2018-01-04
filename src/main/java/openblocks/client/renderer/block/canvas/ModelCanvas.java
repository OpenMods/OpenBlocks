package openblocks.client.renderer.block.canvas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import openmods.model.ModelUpdater;
import openmods.utils.CollectionUtils;

public class ModelCanvas implements IModel {

	public static final IModel INSTANCE = new ModelCanvas(Optional.empty(), ImmutableSet.of());

	private final Optional<ResourceLocation> baseModel;

	private final Set<BlockRenderLayer> baseModelRenderLayers;

	private ModelCanvas(Optional<ResourceLocation> baseModel, Set<BlockRenderLayer> baseModelRenderLayers) {
		this.baseModel = baseModel;
		this.baseModelRenderLayers = ImmutableSet.copyOf(baseModelRenderLayers);
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return CollectionUtils.asSet(baseModel);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		final ImmutableMap<TransformType, TRSRTransformation> transforms = PerspectiveMapWrapper.getTransforms(state);

		final IModel base;
		if (baseModel.isPresent()) {
			base = ModelLoaderRegistry.getModelOrLogError(baseModel.get(), "Couldn't load canvas base: " + baseModel.get());
		} else {
			base = ModelLoaderRegistry.getMissingModel();
		}

		final IBakedModel bakedBaseModel = base.bake(state, format, bakedTextureGetter);

		return new BakedModelCanvas(bakedBaseModel, baseModelRenderLayers, transforms, bakedTextureGetter, format);
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		final ModelUpdater updater = new ModelUpdater(customData);

		final Optional<ResourceLocation> base = updater.get("base", ModelUpdater.MODEL_LOCATION, this.baseModel);

		final Set<BlockRenderLayer> layers = updater.get("baseLayers", ModelUpdater.enumConverter(BlockRenderLayer.class), this.baseModelRenderLayers);

		return updater.hasChanged()? new ModelCanvas(base, layers) : this;
	}

}
