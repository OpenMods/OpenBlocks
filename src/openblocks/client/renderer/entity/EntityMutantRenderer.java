package openblocks.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import openblocks.common.entity.EntityMutant;

public class EntityMutantRenderer extends RenderLiving {

	public EntityMutantRenderer(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

	@Override
    protected void bindEntityTexture(Entity par1Entity) { }
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) { return null; }

    protected float getWingRotation(EntityMutant mutant, float par2) {
        //float f1 = mutant.field_70888_h + (mutant.field_70886_e - mutant.field_70888_h) * par2;
        //float f2 = mutant.field_70884_g + (mutant.destPos - mutant.field_70884_g) * par2;
        return 0f;//(MathHelper.sin(f1) + 1.0F) * f2;
    }
	
	@Override
    protected float handleRotationFloat(EntityLivingBase entityLiving, float par2) {
        return getWingRotation((EntityMutant)entityLiving, par2);
    }


}
