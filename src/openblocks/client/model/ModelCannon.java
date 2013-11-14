package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import openblocks.common.tileentity.TileEntityCannon;

public class ModelCannon extends ModelBase {

	ModelRenderer body;
	ModelRenderer shooter;
	ModelRenderer base;
	ModelRenderer wheel;
	
	private float deg30 = (float)Math.toRadians(30);
	private float deg180 = (float)Math.toRadians(180);

	public ModelCannon() {
		textureWidth = 64;
		textureHeight = 32;

		body = new ModelRenderer(this, 0, 0);
		body.addBox(-3F, -5F, -3F, 6, 6, 6);
		body.setRotationPoint(0F, 11F, 3F);
		body.setTextureSize(64, 32);
		body.mirror = true;
		setRotation(body, 0.3490659F, 0F, 0F);
		shooter = new ModelRenderer(this, 34, 0);
		shooter.addBox(-2F, -4F, -2F, 4, 4, 10);
		shooter.setRotationPoint(0F, 11F, 3F);
		shooter.setTextureSize(64, 32);
		shooter.mirror = true;
		setRotation(shooter, 0.3490659F, 0F, 0F);
		base = new ModelRenderer(this, 14, 19);
		base.addBox(-6F, 0F, -6F, 12, 1, 12);
		base.setRotationPoint(0F, 15F, 0F);
		base.setTextureSize(64, 32);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		wheel = new ModelRenderer(this, 0, 20);
		wheel.addBox(3F, -3F, -3F, 1, 6, 6);
		wheel.setRotationPoint(0F, 11F, 3F);
		wheel.setTextureSize(64, 32);
		wheel.mirror = true;
		setRotation(wheel, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {

		float f5 = 0.0625F;
		setRotationAngles(te, f);
		body.render(f5);
		shooter.render(f5);
		base.render(f5);

		float startAngleX = 0;
		
		wheel.rotateAngleZ = 0;
		wheel.rotateAngleX = startAngleX;
		wheel.rotationPointX = 0;
		wheel.render(f5);
		wheel.rotateAngleX += deg30;
		wheel.rotationPointX -= 0.01;
		wheel.render(f5);
		wheel.rotateAngleX += deg30;
		wheel.rotationPointX -= 0.01;
		wheel.render(f5);
		
		wheel.rotateAngleZ = deg180;
		wheel.rotateAngleX = startAngleX;
		wheel.rotationPointX = 0;
		wheel.render(f5);
		wheel.rotateAngleX += deg30;
		wheel.rotationPointX -= 0.01;
		wheel.render(f5);
		wheel.rotateAngleX += deg30;
		wheel.rotationPointX -= 0.01;
		wheel.render(f5);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntity te, float f) {
		TileEntityCannon cannon = (TileEntityCannon)te;
	}

}
