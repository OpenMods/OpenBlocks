package openblocks.client.renderer.item.stencil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;
import openblocks.OpenBlocks;
import openblocks.common.IStencilPattern;
import openblocks.common.StencilPattern;
import openblocks.common.item.ItemStencil;

public class StencilItemOverride extends ItemOverrideList {

	public static final ResourceLocation BACKGROUND_TEXTURE = OpenBlocks.location("items/stencil");

	private final IModelState state;

	private final VertexFormat format;

	private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

	private final IBakedModel emptyBakedModel;

	private final Map<IStencilPattern, IBakedModel> filledModels = Maps.newHashMap();

	public StencilItemOverride(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		super(Collections.emptyList());
		this.state = state;
		this.format = format;
		this.bakedTextureGetter = bakedTextureGetter;

		final ResourceLocation emptyStencilTexture = StencilTextureManager.INSTANCE.getEmptyStencilTextureLocation(BACKGROUND_TEXTURE);
		this.emptyBakedModel = createItemModel(emptyStencilTexture);
	}

	private IBakedModel createItemModel(ResourceLocation texture) {
		return new ItemLayerModel(ImmutableList.of(texture), this).bake(state, format, bakedTextureGetter);
	}

	public IBakedModel getEmptyModel() {
		return emptyBakedModel;
	}

	@Override
	public IBakedModel handleItemState(IBakedModel originalModel, @Nonnull ItemStack stack, World world, LivingEntity entity) {
		final Optional<StencilPattern> pattern = ItemStencil.getPattern(stack);
		if (pattern.isPresent()) {
			return getModelForPattern(pattern.get());
		} else {
			return emptyBakedModel;
		}
	}

	private IBakedModel getModelForPattern(IStencilPattern pattern) {
		IBakedModel model = filledModels.get(pattern);
		if (model == null) {
			model = bakeModel(pattern);
			filledModels.put(pattern, model);
		}
		return model;
	}

	private IBakedModel bakeModel(IStencilPattern pattern) {
		final ResourceLocation stencilTexture = StencilTextureManager.INSTANCE.getStencilTextureLocation(BACKGROUND_TEXTURE, pattern);
		return createItemModel(stencilTexture);
	}

}
