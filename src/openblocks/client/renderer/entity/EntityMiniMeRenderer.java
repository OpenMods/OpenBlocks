package openblocks.client.renderer.entity;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelMiniMe;
import openblocks.common.entity.EntityMiniMe;

public class EntityMiniMeRenderer extends RendererLivingEntity {

	@SuppressWarnings("unused")
	private static final ResourceLocation steveTextures = new ResourceLocation(
			"textures/entity/steve.png");

	public EntityMiniMeRenderer() {
		super(new ModelMiniMe(0.0F), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return ((EntityMiniMe)entity).getLocationSkin();
	}

	@Override
	protected void renderLivingLabel(EntityLivingBase par1EntityLivingBase, String par2Str, double par3, double par5, double par7, int par9) {

	}
}
