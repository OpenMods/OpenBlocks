package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityCannon;

public class ModelCannon extends ModelBase
{
	ModelRenderer main;
	ModelRenderer gun4;
	ModelRenderer gun1;
	ModelRenderer gun2;
	ModelRenderer gun3;
	ModelRenderer shaft;
	ModelRenderer base;

	public ModelCannon()
	{
		textureWidth = 64;
		textureHeight = 32;

		main = new ModelRenderer(this, 0, 0);
		main.addBox(-7F, -2F, -2F, 14, 5, 5);
		main.setRotationPoint(0F, 6F, 0F);
		main.setTextureSize(64, 32);
		main.mirror = true;
		setRotation(main, 0F, 0F, 0F);
		gun4 = new ModelRenderer(this, 0, 10);
		gun4.addBox(4.5F, -1.5F, -8F, 1, 1, 6);
		gun4.setRotationPoint(0F, 6F, 0F);
		gun4.setTextureSize(64, 32);
		gun4.mirror = true;
		setRotation(gun4, 0F, 0F, 0F);
		gun1 = new ModelRenderer(this, 0, 10);
		gun1.addBox(-5.5F, -1.5F, -8F, 1, 1, 6);
		gun1.setRotationPoint(0F, 6F, 0F);
		gun1.setTextureSize(64, 32);
		gun1.mirror = true;
		setRotation(gun1, 0F, 0F, 0F);
		gun2 = new ModelRenderer(this, 0, 10);
		gun2.addBox(-5.5F, 1.5F, -8F, 1, 1, 6);
		gun2.setRotationPoint(0F, 6F, 0F);
		gun2.setTextureSize(64, 32);
		gun2.mirror = true;
		setRotation(gun2, 0F, 0F, 0F);
		gun3 = new ModelRenderer(this, 0, 10);
		gun3.addBox(4.5F, 1.5F, -8F, 1, 1, 6);
		gun3.setRotationPoint(0F, 6F, 0F);
		gun3.setTextureSize(64, 32);
		gun3.mirror = true;
		setRotation(gun3, 0F, 0F, 0F);
		shaft = new ModelRenderer(this, 0, 17);
		shaft.addBox(-1F, 0F, -1F, 2, 8, 2);
		shaft.setRotationPoint(0F, 7F, 0F);
		shaft.setTextureSize(64, 32);
		shaft.mirror = true;
		setRotation(shaft, 0F, 0F, 0F);
		base = new ModelRenderer(this, 8, 17);
		base.addBox(-5F, 0F, -5F, 10, 1, 10);
		base.setRotationPoint(0F, 15F, 0F);
		base.setTextureSize(64, 32);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {

		float f5 = 0.0625F;
		setRotationAngles(te, f);
		main.render(f5);
		gun1.render(f5);
		gun2.render(f5);
		gun3.render(f5);
		gun4.render(f5);
		base.render(f5);
		shaft.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntity te, float f) {
		TileEntityCannon cannon = (TileEntityCannon)te;
		main.rotateAngleX = (float)Math.toRadians(cannon.pitch.getValue());
		gun1.rotateAngleX = gun2.rotateAngleX = gun3.rotateAngleX = gun4.rotateAngleX = main.rotateAngleX;
		main.rotateAngleZ = 0;
		gun1.rotateAngleZ = gun2.rotateAngleZ = gun3.rotateAngleZ = gun4.rotateAngleZ = main.rotateAngleZ;

		main.rotateAngleY = (float)Math.toRadians(cannon.yaw.getValue());
		gun1.rotateAngleY = gun2.rotateAngleY = gun3.rotateAngleY = gun4.rotateAngleY = main.rotateAngleY;
	}

}
