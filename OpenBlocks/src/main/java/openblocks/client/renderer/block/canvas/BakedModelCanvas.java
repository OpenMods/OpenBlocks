package openblocks.client.renderer.block.canvas;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import openmods.model.BakedModelAdapter;

public class BakedModelCanvas extends BakedModelAdapter {

	private final StencilModelTransformer modelTransformer;

	public BakedModelCanvas(IBakedModel baseModel, Set<BlockRenderLayer> baseModelLayers, ImmutableMap<TransformType, TRSRTransformation> transforms, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, VertexFormat vertexFormat) {
		super(baseModel, transforms);
		this.modelTransformer = new StencilModelTransformer(baseModel, baseModelLayers, bakedTextureGetter, vertexFormat);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, long rand) {
		final Optional<BlockState> maybeInnerBlock;
		final Optional<CanvasState> maybeCanvasState;
		if (state instanceof IExtendedBlockState) {
			final IExtendedBlockState extendedState = (IExtendedBlockState)state;
			final ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties = extendedState.getUnlistedProperties();
			maybeInnerBlock = getProperty(unlistedProperties, InnerBlockState.PROPERTY);
			maybeCanvasState = getProperty(unlistedProperties, CanvasState.PROPERTY);

			maybeCanvasState.ifPresent(CanvasState::onRender);
		} else {
			maybeInnerBlock = Optional.empty();
			maybeCanvasState = Optional.empty();
		}

		final BlockRenderLayer renderLayer = MoreObjects.firstNonNull(MinecraftForgeClient.getRenderLayer(), BlockRenderLayer.CUTOUT);

		return modelTransformer.getQuads(maybeInnerBlock, maybeCanvasState, renderLayer).get(side);

	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<T> getProperty(ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties, IUnlistedProperty<T> prop) {
		return (Optional<T>)unlistedProperties.get(prop);
	}

}
