package openblocks.client.renderer.item.stencil;

import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

public class ModelStencil implements IModel {
	public static final ModelStencil INSTANCE = new ModelStencil();

	public ModelStencil() {}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		final StencilItemOverride override = new StencilItemOverride(state, format, bakedTextureGetter);
		return override.getEmptyModel();
	}
}
