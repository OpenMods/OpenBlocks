package openblocks.client.renderer.entity;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import openblocks.common.entity.EntityMiniMe;

public class EntityMiniMeRenderer extends BipedRenderer<EntityMiniMe> {

	public EntityMiniMeRenderer(EntityRendererManager renderManager) {
		super(renderManager, new ModelPlayer(0.5f, false), 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMiniMe entity) {
		return entity.getSkinResourceLocation();
	}

}
