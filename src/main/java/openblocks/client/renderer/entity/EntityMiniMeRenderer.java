package openblocks.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelMiniMe;
import openblocks.common.entity.EntityMiniMe;

public class EntityMiniMeRenderer extends RenderLiving {

	public EntityMiniMeRenderer() {
		super(new ModelMiniMe(0.0F), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return ((EntityMiniMe)entity).getLocationSkin();
	}

}
