package openblocks.client.model;

import openblocks.common.tileentity.TileEntityGoldenEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;

public class ModelEgg extends ModelBase {
	// fields
	ModelRenderer egg1;
	ModelRenderer egg6;
	ModelRenderer egg2;
	ModelRenderer egg3;
	ModelRenderer egg4;
	ModelRenderer egg5;

	public ModelEgg() {
		textureWidth = 128;
		textureHeight = 64;

		egg1 = new ModelRenderer(this, 0, 0);
		egg1.addBox(-2F, -16F, -2F, 4, 1, 4);
		egg1.setRotationPoint(0F, 16F, 0F);
		egg1.setTextureSize(128, 64);
		egg1.mirror = true;
		setRotation(egg1, 0F, 0F, 0F);
		egg2 = new ModelRenderer(this, 0, 5);
		egg2.addBox(-3F, -15F, -3F, 6, 15, 6);
		egg2.setRotationPoint(0F, 16F, 0F);
		egg2.setTextureSize(128, 64);
		egg2.mirror = true;
		setRotation(egg2, 0F, 0F, 0F);
		egg3 = new ModelRenderer(this, 0, 26);
		egg3.addBox(-4F, -14F, -4F, 8, 1, 8);
		egg3.setRotationPoint(0F, 16F, 0F);
		egg3.setTextureSize(128, 64);
		egg3.mirror = true;
		setRotation(egg3, 0F, 0F, 0F);
		egg4 = new ModelRenderer(this, 0, 35);
		egg4.addBox(-5F, -13F, -5F, 10, 2, 10);
		egg4.setRotationPoint(0F, 16F, 0F);
		egg4.setTextureSize(128, 64);
		egg4.mirror = true;
		setRotation(egg4, 0F, 0F, 0F);
		egg5 = new ModelRenderer(this, 40, 0);
		egg5.addBox(-6F, -11F, -6F, 12, 10, 12);
		egg5.setRotationPoint(0F, 16F, 0F);
		egg5.setTextureSize(128, 64);
		egg5.mirror = true;
		setRotation(egg5, 0F, 0F, 0F);
		egg6 = new ModelRenderer(this, 40, 22);
		egg6.addBox(-7F, -8F, -7F, 14, 5, 14);
		egg6.setRotationPoint(0F, 16F, 0F);
		egg6.setTextureSize(128, 64);
		egg6.mirror = true;
		setRotation(egg6, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {
		float f5 = 0.0625F;
		setRotationAngles(te, f);
		egg1.render(f5);
		egg2.render(f5);
		egg3.render(f5);
		egg4.render(f5);
		egg5.render(f5);
		egg6.render(f5);
	}

	public void setRotationAngles(TileEntity te, float f) {
		TileEntityGoldenEgg egg = (TileEntityGoldenEgg) te;
		
	}
	

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
