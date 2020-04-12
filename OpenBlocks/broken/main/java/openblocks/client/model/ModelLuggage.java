package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelLuggage extends ModelBase {
	// fields
	private final ModelRenderer lid;
	private final ModelRenderer leg1;
	private final ModelRenderer body;

	public ModelLuggage() {
		textureWidth = 128;
		textureHeight = 64;

		lid = new ModelRenderer(this, 0, 23);
		lid.addBox(-8F, -2F, -8F, 16, 2, 8);
		lid.setRotationPoint(0F, 13F, 4F);
		lid.setTextureSize(128, 64);
		lid.mirror = true;
		setRotation(lid, 0F, 0F, 0F);
		leg1 = new ModelRenderer(this, 0, 41);
		leg1.addBox(-0.5F, 0F, -0.5F, 1, 4, 1);
		leg1.setRotationPoint(-7.5F, 20F, 3F);
		leg1.setTextureSize(128, 64);
		leg1.mirror = true;
		setRotation(leg1, 0F, 0F, 0F);
		body = new ModelRenderer(this, 0, 0);
		body.addBox(-8F, 0F, -4F, 16, 7, 8);
		body.setRotationPoint(0F, 13F, 0F);
		body.setTextureSize(128, 64);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		lid.render(f5);
		body.render(f5);
		for (int x = -3; x <= 3; x++) {
			for (int z = -1; z <= 1; z++) {
				leg1.setRotationPoint((float)x * 2, 20F, (float)z * 2);
				leg1.rotateAngleX = MathHelper.cos(f + (x * z) * 0.6662F)
						* 1.4F * f1;
				leg1.render(f5);
			}
		}
	}

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
		lid.rotateAngleX = Math.min(0, MathHelper.cos(par1 * 0.6662F) * 1.4F
				* par2);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
