package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityDonationStation;

public class ModelPiggy extends ModelBase {
	// fields
	ModelRenderer body;
	ModelRenderer head;
	ModelRenderer snout;
	ModelRenderer ear2;
	ModelRenderer ear1;
	ModelRenderer leg4;
	ModelRenderer leg2;
	ModelRenderer leg1;
	ModelRenderer leg3;
	ModelRenderer tail;

	public ModelPiggy()
	{
		textureWidth = 64;
		textureHeight = 64;

		body = new ModelRenderer(this, 0, 0);
		body.addBox(-5F, 2F, -5F, 10, 10, 11);
		body.setRotationPoint(0F, 0F, 0F);
		body.setTextureSize(64, 64);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
		head = new ModelRenderer(this, 0, 21);
		head.addBox(-3.5F, -2F, -3F, 7, 7, 3);
		head.setRotationPoint(0F, 4F, -5F);
		head.setTextureSize(64, 64);
		head.mirror = true;
		setRotation(head, 0F, 0F, 0F);
		snout = new ModelRenderer(this, 0, 31);
		snout.addBox(-2F, 0F, -4F, 4, 4, 1);
		snout.setRotationPoint(0F, 4F, -5F);
		snout.setTextureSize(64, 64);
		snout.mirror = true;
		setRotation(snout, 0F, 0F, 0F);
		ear2 = new ModelRenderer(this, 0, 40);
		ear2.addBox(0F, -5F, 0F, 3, 3, 1);
		ear2.setRotationPoint(0F, 4F, -5F);
		ear2.setTextureSize(64, 64);
		ear2.mirror = true;
		setRotation(ear2, 0.4363323F, -0.0523599F, 0.6108652F);
		ear1 = new ModelRenderer(this, 0, 36);
		ear1.addBox(-3F, -5F, 0F, 3, 3, 1);
		ear1.setRotationPoint(0F, 4F, -5F);
		ear1.setTextureSize(64, 64);
		ear1.mirror = true;
		setRotation(ear1, 0.4363323F, -0.0523599F, -0.6108652F);
		leg4 = new ModelRenderer(this, 20, 45);
		leg4.addBox(-1.5F, 0F, -1.5F, 3, 5, 3);
		leg4.setRotationPoint(-3F, 11F, 3F);
		leg4.setTextureSize(64, 64);
		leg4.mirror = true;
		setRotation(leg4, 0F, 0F, 0.122173F);
		leg2 = new ModelRenderer(this, 20, 21);
		leg2.addBox(-1.5F, 0F, -1.5F, 3, 5, 3);
		leg2.setRotationPoint(-3F, 11F, -3F);
		leg2.setTextureSize(64, 64);
		leg2.mirror = true;
		setRotation(leg2, 0F, 0F, 0.122173F);
		leg1 = new ModelRenderer(this, 20, 29);
		leg1.addBox(-1.5F, 0F, -1.5F, 3, 5, 3);
		leg1.setRotationPoint(3F, 11F, -3F);
		leg1.setTextureSize(64, 64);
		leg1.mirror = true;
		setRotation(leg1, 0F, 0F, -0.2268928F);
		leg3 = new ModelRenderer(this, 20, 37);
		leg3.addBox(-1.5F, 0F, -1.5F, 3, 5, 3);
		leg3.setRotationPoint(3F, 11F, 3F);
		leg3.setTextureSize(64, 64);
		leg3.mirror = true;
		setRotation(leg3, 0F, 0F, -0.1745329F);
		tail = new ModelRenderer(this, 0, 44);
		tail.addBox(-0.5F, 0F, 0F, 1, 3, 2);
		tail.setRotationPoint(0F, 1F, 5F);
		tail.setTextureSize(64, 64);
		tail.mirror = true;
		setRotation(tail, 0F, 0.2617994F, 0.2268928F);
	}

	public void render(TileEntityDonationStation station, float f) {
		float f5 = 0.0625F;
		setRotationAngles(station, f);
		body.render(f5);
		head.render(f5);
		snout.render(f5);
		ear2.render(f5);
		ear1.render(f5);
		leg4.render(f5);
		leg2.render(f5);
		leg1.render(f5);
		leg3.render(f5);
		tail.render(f5);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@SuppressWarnings("unused")
	public void setRotationAngles(TileEntity te, float f) {}
}
