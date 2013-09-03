package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityCannon;

public class ModelCannon extends ModelBase
{
	ModelRenderer shaft;
	ModelRenderer shaft2;
	ModelRenderer main;
	ModelRenderer stand;
	ModelRenderer base;
	ModelRenderer pipe1;
	ModelRenderer pipe2;
	ModelRenderer maintop;
	ModelRenderer rest;
	ModelRenderer handle;

	public ModelCannon()
	{
		textureWidth = 64;
		textureHeight = 32;

		shaft = new ModelRenderer(this, 12, 11);
		shaft.addBox(-1F, 0.5F, 0F, 2, 1, 8);
		shaft.setRotationPoint(0F, 5F, 0F);
		shaft.setTextureSize(64, 32);
		shaft.mirror = true;
		setRotation(shaft, 0F, 0F, 0F);
		shaft2 = new ModelRenderer(this, 0, 11);
		shaft2.addBox(-1.5F, 0F, 5F, 3, 2, 1);
		shaft2.setRotationPoint(0F, 5F, 0F);
		shaft2.setTextureSize(64, 32);
		shaft2.mirror = true;
		setRotation(shaft2, 0F, 0F, 0F);
		main = new ModelRenderer(this, 16, 20);
		main.addBox(-1.5F, 0F, -5F, 3, 3, 6);
		main.setRotationPoint(0F, 5F, 0F);
		main.setTextureSize(64, 32);
		main.mirror = true;
		setRotation(main, 0F, 0F, 0F);
		stand = new ModelRenderer(this, 0, 17);
		stand.addBox(-0.5F, 1F, 0F, 1, 9, 1);
		stand.setRotationPoint(0F, 5F, 0F);
		stand.setTextureSize(64, 32);
		stand.mirror = true;
		setRotation(stand, -0.3490659F, 0F, 0F);
		base = new ModelRenderer(this, 0, 0);
		base.addBox(-5F, 9F, -5F, 10, 1, 10);
		base.setRotationPoint(0F, 5F, 0F);
		base.setTextureSize(64, 32);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		pipe1 = new ModelRenderer(this, 4, 17);
		pipe1.addBox(0.5F, 1F, -6F, 2, 1, 1);
		pipe1.setRotationPoint(0F, 5F, 0F);
		pipe1.setTextureSize(64, 32);
		pipe1.mirror = true;
		setRotation(pipe1, 0F, 0F, 0F);
		pipe2 = new ModelRenderer(this, 4, 19);
		pipe2.addBox(1.5F, 1F, -5F, 1, 1, 1);
		pipe2.setRotationPoint(0F, 5F, 0F);
		pipe2.setTextureSize(64, 32);
		pipe2.mirror = true;
		setRotation(pipe2, 0F, 0F, 0F);
		maintop = new ModelRenderer(this, 4, 21);
		maintop.addBox(-1F, -0.5F, -4F, 2, 1, 4);
		maintop.setRotationPoint(0F, 5F, 0F);
		maintop.setTextureSize(64, 32);
		maintop.mirror = true;
		setRotation(maintop, 0F, 0F, 0F);
		rest = new ModelRenderer(this, 0, 15);
		rest.addBox(-2.5F, 7F, -0.2F, 5, 1, 1);
		rest.setRotationPoint(0F, 5F, 0F);
		rest.setTextureSize(64, 32);
		rest.mirror = true;
		setRotation(rest, -0.3490659F, 0F, 0F);
		handle = new ModelRenderer(this, 0, 29);
		handle.addBox(-5F, 1F, -2F, 10, 1, 1);
		handle.setRotationPoint(0F, 5F, 0F);
		handle.setTextureSize(64, 32);
		handle.mirror = true;
		setRotation(handle, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {

		float f5 = 0.0625F;
		setRotationAngles(te, f);
		shaft.render(f5);
		shaft2.render(f5);
		main.render(f5);
		stand.render(f5);
		base.render(f5);
		pipe1.render(f5);
		pipe2.render(f5);
		maintop.render(f5);
		rest.render(f5);
		handle.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntity te, float f) {
		TileEntityCannon cannon = (TileEntityCannon)te;
		main.rotateAngleX = (float)Math.toRadians(-cannon.pitch.getValue());
		maintop.rotateAngleX = rest.rotateAngleX = stand.rotateAngleX = handle.rotateAngleX = shaft.rotateAngleX = shaft2.rotateAngleX = pipe1.rotateAngleX = pipe2.rotateAngleX = main.rotateAngleX;
		main.rotateAngleZ = 0;
		stand.rotateAngleZ = 0;
		maintop.rotateAngleZ = rest.rotateAngleZ = handle.rotateAngleZ = shaft.rotateAngleZ = shaft2.rotateAngleZ = pipe1.rotateAngleZ = pipe2.rotateAngleZ = main.rotateAngleZ;

		main.rotateAngleY = (float)Math.toRadians(cannon.yaw.getValue());
		maintop.rotateAngleY = rest.rotateAngleY = stand.rotateAngleY = handle.rotateAngleY = shaft.rotateAngleY = shaft2.rotateAngleY = pipe1.rotateAngleY = pipe2.rotateAngleY = main.rotateAngleY;
	}

}
