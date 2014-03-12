package openblocks.client.renderer.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityMiniMeRenderer extends RendererLivingEntity {

	private static final ResourceLocation steveTextures = new ResourceLocation(
			"textures/entity/steve.png");

	public EntityMiniMeRenderer() {
		super(new ModelBiped(0.0F), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return steveTextures;
	}
}
