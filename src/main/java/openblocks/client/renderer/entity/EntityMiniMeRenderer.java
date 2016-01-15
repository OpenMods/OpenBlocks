package openblocks.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelMiniMe;
import openblocks.common.entity.EntityMiniMe;

public class EntityMiniMeRenderer extends RenderBiped<EntityMiniMe> {

	public EntityMiniMeRenderer(RenderManager renderManager) {
		super(renderManager, new ModelMiniMe(0.5F), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMiniMe entity) {
		return entity.getSkinResourceLocation();
	}

}
