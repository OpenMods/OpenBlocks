package openblocks.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;

public class ModelFan extends ModelBase {

	ModelRenderer outline1;
	ModelRenderer outline2;
	ModelRenderer outline3;
	ModelRenderer outline4;
	ModelRenderer outline5;
	ModelRenderer outline6;
	ModelRenderer outline7;
	ModelRenderer outline8;
	ModelRenderer stand;
	ModelRenderer base;
	ModelRenderer fan;

	public ModelFan()
	{
		textureWidth = 32;
		textureHeight = 32;
		outline1 = new ModelRenderer(this, 0, 0);
		outline1.addBox(-0.5F, 0F, -0.5F, 1, 4, 2);
		outline1.setRotationPoint(3.54F, 8.54F, 0F);
		outline1.setTextureSize(32, 32);
		outline1.mirror = true;
		setRotation(outline1, 0F, 0F, -2.786124F);
		outline2 = new ModelRenderer(this, 0, 0);
		outline2.addBox(-0.5F, 0F, -0.5F, 1, 4, 2);
		outline2.setRotationPoint(-5F, 5F, 0F);
		outline2.setTextureSize(32, 32);
		outline2.mirror = true;
		setRotation(outline2, 0F, 0F, -0.4112368F);
		outline3 = new ModelRenderer(this, 0, 0);
		outline3.addBox(-0.5F, 0F, -0.5F, 1, 4, 2);
		outline3.setRotationPoint(0F, 10F, 0F);
		outline3.setTextureSize(32, 32);
		outline3.mirror = true;
		setRotation(outline3, 0F, 0F, -2.009917F);
		outline4 = new ModelRenderer(this, 0, 0);
		outline4.addBox(-0.5F, 0F, -0.5F, 1, 4, 2);
		outline4.setRotationPoint(-3.54F, 8.54F, 0F);
		outline4.setTextureSize(32, 32);
		outline4.mirror = true;
		setRotation(outline4, 0F, 0F, -1.187443F);
		outline5 = new ModelRenderer(this, 0, 0);
		outline5.addBox(-0.5F, 0F, -0.5F, 1, 4, 2);
		outline5.setRotationPoint(5F, 5F, 0F);
		outline5.setTextureSize(32, 32);
		outline5.mirror = true;
		setRotation(outline5, 0F, 0F, 2.751217F);
		outline6 = new ModelRenderer(this, 0, 0);
		outline6.addBox(-0.5F, 0F, -0.5F, 1, 4, 2);
		outline6.setRotationPoint(0F, 0F, 0F);
		outline6.setTextureSize(32, 32);
		outline6.mirror = true;
		setRotation(outline6, 0F, 0F, 1.172262F);
		outline7 = new ModelRenderer(this, 0, 0);
		outline7.addBox(-0.5F, 0F, -0.5F, 1, 4, 2);
		outline7.setRotationPoint(3.54F, 1.46F, 0F);
		outline7.setTextureSize(32, 32);
		outline7.mirror = true;
		setRotation(outline7, 0F, 0F, 1.968194F);
		stand = new ModelRenderer(this, 0, 6);
		stand.addBox(-0.5F, -10F, -0.5F, 1, 10, 1);
		stand.setRotationPoint(0F, 15F, -2F);
		stand.setTextureSize(32, 32);
		stand.mirror = true;
		setRotation(stand, -0.1115358F, 0F, 0F);
		outline8 = new ModelRenderer(this, 0, 0);
		outline8.addBox(-0.5F, 0F, -0.5F, 1, 4, 2);
		outline8.setRotationPoint(-3.54F, 1.46F, 0F);
		outline8.setTextureSize(32, 32);
		outline8.mirror = true;
		setRotation(outline8, 0F, 0F, 0.3391516F);
		base = new ModelRenderer(this, 6, 0);
		base.addBox(-3F, 0F, -3F, 6, 1, 6);
		base.setRotationPoint(0F, 15F, -2F);
		base.setTextureSize(32, 32);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		fan = new ModelRenderer(this, 4, 7);
		fan.addBox(-5F, -5F, 0F, 10, 10, 1);
		fan.setRotationPoint(0F, 5F, -0.4F);
		fan.setTextureSize(32, 32);
		fan.mirror = true;
		setRotation(fan, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {

		float f5 = 0.0625F;
		setRotationAngles(te, f);
		outline1.render(f5);
		outline2.render(f5);
		outline3.render(f5);
		outline4.render(f5);
		outline5.render(f5);
		outline6.render(f5);
		outline7.render(f5);
		outline8.render(f5);
		stand.render(f5);
		base.render(f5);
		fan.render(f5);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntity te, float f) {
		fan.rotateAngleZ = (float)Math.toRadians(Minecraft.getSystemTime() % 360);
	}

}
