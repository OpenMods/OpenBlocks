package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;

public class ModelPaintMixer extends ModelBase
{
	// fields
	ModelRenderer controls;
	ModelRenderer right;
	ModelRenderer left;
	ModelRenderer back;
	ModelRenderer top;
	ModelRenderer bottom;
	ModelRenderer bottom2;

	public ModelPaintMixer()
	{
		textureWidth = 64;
		textureHeight = 64;

		controls = new ModelRenderer(this, 22, 20);
		controls.addBox(-5F, 0F, -5F, 10, 4, 1);
		controls.setRotationPoint(0F, 2F, 0F);
		controls.setTextureSize(64, 64);
		controls.mirror = true;
		setRotation(controls, -0.2928848F, 0F, 0F);
		right = new ModelRenderer(this, 0, 36);
		right.addBox(-6F, 0F, -6F, 1, 16, 12);
		right.setRotationPoint(0F, 0F, 0F);
		right.setTextureSize(64, 64);
		right.mirror = true;
		setRotation(right, 0F, 3.141593F, 0F);
		left = new ModelRenderer(this, 0, 36);
		left.addBox(-6F, 0F, -6F, 1, 16, 12);
		left.setRotationPoint(0F, 0F, 0F);
		left.setTextureSize(64, 64);
		left.mirror = true;
		setRotation(left, 0F, 0F, 0F);
		back = new ModelRenderer(this, 0, 19);
		back.addBox(-5F, 0F, 5F, 10, 16, 1);
		back.setRotationPoint(0F, 0F, 0F);
		back.setTextureSize(64, 64);
		back.mirror = true;
		setRotation(back, 0F, 0F, 0F);
		top = new ModelRenderer(this, 22, 25);
		top.addBox(-5F, 0F, -10F, 10, 1, 10);
		top.setRotationPoint(0F, 0F, 5F);
		top.setTextureSize(64, 64);
		top.mirror = true;
		setRotation(top, 0.1115358F, 0F, 0F);
		bottom = new ModelRenderer(this, 0, 0);
		bottom.addBox(-5F, 14F, -7F, 10, 1, 12);
		bottom.setRotationPoint(0F, 0F, 0F);
		bottom.setTextureSize(64, 64);
		bottom.mirror = true;
		setRotation(bottom, 0F, 0F, 0F);
		bottom2 = new ModelRenderer(this, 26, 36);
		bottom2.addBox(-5F, 10F, -6F, 10, 4, 1);
		bottom2.setRotationPoint(0F, 0F, 0F);
		bottom2.setTextureSize(64, 64);
		bottom2.mirror = true;
		setRotation(bottom2, 0F, 0F, 0F);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@SuppressWarnings("unused")
	public void render(TileEntity te, float f) {
		float f5 = 0.0625F;
		controls.render(f5);
		right.render(f5);
		left.render(f5);
		back.render(f5);
		top.render(f5);
		bottom.render(f5);
		bottom2.render(f5);
	}
}
