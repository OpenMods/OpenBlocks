package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;

public class ModelOreCrusher extends ModelBase {
	ModelRenderer crusherBase;
	ModelRenderer crusherHead;
	ModelRenderer crusherArmA;
	ModelRenderer crusherContainer;
	ModelRenderer crusherArmB;
	ModelRenderer frameA;
	ModelRenderer frameB;
	ModelRenderer frameC;
	ModelRenderer frameD;

	public ModelOreCrusher() {
		textureWidth = 128;
		textureHeight = 64;

		crusherBase = new ModelRenderer(this, 0, 0);
		crusherBase.addBox(-8F, 0F, -8F, 16, 3, 16);
		crusherBase.setRotationPoint(0F, 21F, 0F);
		crusherBase.setTextureSize(128, 64);
		crusherBase.mirror = true;
		setRotation(crusherBase, 0F, 0F, 0F);
		crusherHead = new ModelRenderer(this, 0, 19);
		crusherHead.addBox(-7F, 0F, -7F, 14, 6, 14);
		crusherHead.setRotationPoint(0F, 15F, 0F);
		crusherHead.setTextureSize(128, 64);
		crusherHead.mirror = true;
		setRotation(crusherHead, 0F, 0F, 0F);
		crusherArmA = new ModelRenderer(this, 64, 24);
		crusherArmA.addBox(-2F, 0F, -2F, 4, 7, 4);
		crusherArmA.setRotationPoint(0F, 8F, 0F);
		crusherArmA.setTextureSize(128, 64);
		crusherArmA.mirror = true;
		setRotation(crusherArmA, 0F, 0.7853982F, 0F);
		crusherContainer = new ModelRenderer(this, 64, 0);
		crusherContainer.addBox(-7F, 0F, -7F, 14, 8, 14);
		crusherContainer.setRotationPoint(0F, 0F, 0F);
		crusherContainer.setTextureSize(128, 64);
		crusherContainer.mirror = true;
		setRotation(crusherContainer, 0F, 0F, 0F);
		crusherArmB = new ModelRenderer(this, 64, 24);
		crusherArmB.addBox(-2F, 0F, -2F, 4, 7, 4);
		crusherArmB.setRotationPoint(0F, 8F, 0F);
		crusherArmB.setTextureSize(128, 64);
		crusherArmB.mirror = true;
		setRotation(crusherArmB, 0F, 0F, 0F);
		frameA = new ModelRenderer(this, 56, 19);
		frameA.addBox(0F, 0F, 0F, 1, 21, 1);
		frameA.setRotationPoint(-8F, 0F, -5F);
		frameA.setTextureSize(128, 64);
		frameA.mirror = true;
		setRotation(frameA, 0F, 0F, 0F);
		frameB = new ModelRenderer(this, 56, 19);
		frameB.addBox(0F, 0F, 0F, 1, 21, 1);
		frameB.setRotationPoint(-8F, 0F, 5F);
		frameB.setTextureSize(128, 64);
		frameB.mirror = true;
		setRotation(frameB, 0F, 0F, 0F);
		frameC = new ModelRenderer(this, 56, 19);
		frameC.addBox(0F, 0F, 0F, 1, 21, 1);
		frameC.setRotationPoint(7F, 0F, 5F);
		frameC.setTextureSize(128, 64);
		frameC.mirror = true;
		setRotation(frameC, 0F, 0F, 0F);
		frameD = new ModelRenderer(this, 56, 19);
		frameD.addBox(0F, 0F, 0F, 1, 21, 1);
		frameD.setRotationPoint(7F, 0F, -5F);
		frameD.setTextureSize(128, 64);
		frameD.mirror = true;
		setRotation(frameD, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {
		float f5 = 0.0625F;
		setRotationAngles(te, f);
		crusherBase.render(f5);
		crusherHead.render(f5);
		crusherArmA.render(f5);
		crusherContainer.render(f5);
		crusherArmB.render(f5);
		frameA.render(f5);
		frameB.render(f5);
		frameC.render(f5);
		frameD.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntity t, float f) {

	}

}
