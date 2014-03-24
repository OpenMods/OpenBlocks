package openblocks.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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

	@Override
	protected void renderLivingLabel(EntityLivingBase par1EntityLivingBase, String par2Str, double par3, double par5, double par7, int par9) {
		super.renderLivingLabel(par1EntityLivingBase, par2Str, par3, par5, par7, par9);
	}
}
