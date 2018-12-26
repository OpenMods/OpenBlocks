package openblocks.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import openblocks.common.entity.EntityMiniMe;

public class ModelMiniMe extends ModelBiped {

	public ModelMiniMe(float par1) {
		super(par1, 0.0F, 64, 32);
	}

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
		EntityMiniMe minime = (EntityMiniMe)entity;
		if (minime.isBeingRidden()) {
			this.bipedLeftArm.rotateAngleX = -3f;
			this.bipedRightArm.rotateAngleX = -3f;
			this.bipedLeftArm.rotateAngleZ = 0.3f;
			this.bipedRightArm.rotateAngleZ = -0.3f;
		}
	}
}
