package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import openblocks.common.tileentity.TileEntityBearTrap;

public class ModelBearTrap extends ModelBase {

	ModelRenderer middle;

	ModelRenderer leftspike5;
	ModelRenderer leftnearside;
	ModelRenderer lefttopside;
	ModelRenderer leftfarside;
	ModelRenderer leftspike2;
	ModelRenderer leftspike1;
	ModelRenderer leftspike3;
	ModelRenderer leftspike4;

	ModelRenderer righttopside;
	ModelRenderer rightfarside;
	ModelRenderer rightnearside;
	ModelRenderer rightspike1;
	ModelRenderer rightspike2;
	ModelRenderer rightspike3;
	ModelRenderer rightspike4;
	ModelRenderer trigger;

	public ModelBearTrap() {

		this.textureWidth = 64;
		this.textureHeight = 32;

		middle = new ModelRenderer(this, 0, 0);
		middle.addBox(-0.5F, 0F, -8F, 1, 1, 16);
		middle.setRotationPoint(0F, 15F, 0F);
		middle.setTextureSize(64, 32);
		middle.mirror = true;
		setRotation(middle, 0F, 0F, 0F);
		righttopside = new ModelRenderer(this, 0, 4);
		righttopside.addBox(-6.5F, 0F, -8F, 6, 1, 1);
		righttopside.setRotationPoint(0F, 15F, 0F);
		righttopside.setTextureSize(64, 32);
		righttopside.mirror = true;
		setRotation(righttopside, 0F, 0F, 0F);
		rightfarside = new ModelRenderer(this, 0, 0);
		rightfarside.addBox(-7.5F, 0F, -8F, 1, 1, 16);
		rightfarside.setRotationPoint(0F, 15F, 0F);
		rightfarside.setTextureSize(64, 32);
		rightfarside.mirror = true;
		setRotation(rightfarside, 0F, 0F, 0F);
		rightnearside = new ModelRenderer(this, 0, 6);
		rightnearside.addBox(-6.5F, 0F, 7F, 6, 1, 1);
		rightnearside.setRotationPoint(0F, 15F, 0F);
		rightnearside.setTextureSize(64, 32);
		rightnearside.mirror = true;
		setRotation(rightnearside, 0F, 0F, 0F);
		rightspike1 = new ModelRenderer(this, 0, 8);
		rightspike1.addBox(-7.5F, -2F, -6F, 1, 2, 1);
		rightspike1.setRotationPoint(0F, 15F, 0F);
		rightspike1.setTextureSize(64, 32);
		rightspike1.mirror = true;
		setRotation(rightspike1, 0F, 0F, 0F);
		rightspike2 = new ModelRenderer(this, 0, 8);
		rightspike2.addBox(-7.5F, -2F, -2F, 1, 2, 1);
		rightspike2.setRotationPoint(0F, 15F, 0F);
		rightspike2.setTextureSize(64, 32);
		rightspike2.mirror = true;
		setRotation(rightspike2, 0F, 0F, 0F);
		rightspike3 = new ModelRenderer(this, 0, 8);
		rightspike3.addBox(-7.5F, -2F, 1.5F, 1, 2, 1);
		rightspike3.setRotationPoint(0F, 15F, 0F);
		rightspike3.setTextureSize(64, 32);
		rightspike3.mirror = true;
		setRotation(rightspike3, 0F, 0F, 0F);
		rightspike4 = new ModelRenderer(this, 0, 8);
		rightspike4.addBox(-7.5F, -2F, 5F, 1, 2, 1);
		rightspike4.setRotationPoint(0F, 15F, 0F);
		rightspike4.setTextureSize(64, 32);
		rightspike4.mirror = true;
		setRotation(rightspike4, 0F, 0F, 0F);
		leftnearside = new ModelRenderer(this, 0, 6);
		leftnearside.addBox(0.5F, 0F, 7F, 6, 1, 1);
		leftnearside.setRotationPoint(0F, 15F, 0F);
		leftnearside.setTextureSize(64, 32);
		leftnearside.mirror = true;
		setRotation(leftnearside, 0F, 0F, 0F);
		lefttopside = new ModelRenderer(this, 0, 4);
		lefttopside.addBox(0.5F, 0F, -8F, 6, 1, 1);
		lefttopside.setRotationPoint(0F, 15F, 0F);
		lefttopside.setTextureSize(64, 32);
		lefttopside.mirror = true;
		setRotation(lefttopside, 0F, 0F, 0F);
		leftfarside = new ModelRenderer(this, 0, 0);
		leftfarside.addBox(6.5F, 0F, -8F, 1, 1, 16);
		leftfarside.setRotationPoint(0F, 15F, 0F);
		leftfarside.setTextureSize(64, 32);
		leftfarside.mirror = true;
		setRotation(leftfarside, 0F, 0F, 0F);
		leftspike2 = new ModelRenderer(this, 0, 8);
		leftspike2.addBox(6.5F, -2F, -4F, 1, 2, 1);
		leftspike2.setRotationPoint(0F, 15F, 0F);
		leftspike2.setTextureSize(64, 32);
		leftspike2.mirror = true;
		setRotation(leftspike2, 0F, 0F, 0F);
		leftspike1 = new ModelRenderer(this, 0, 8);
		leftspike1.addBox(6.5F, -2F, -8F, 1, 2, 1);
		leftspike1.setRotationPoint(0F, 15F, 0F);
		leftspike1.setTextureSize(64, 32);
		leftspike1.mirror = true;
		setRotation(leftspike1, 0F, 0F, 0F);
		leftspike3 = new ModelRenderer(this, 0, 8);
		leftspike3.addBox(6.5F, -2F, -0.5F, 1, 2, 1);
		leftspike3.setRotationPoint(0F, 15F, 0F);
		leftspike3.setTextureSize(64, 32);
		leftspike3.mirror = true;
		setRotation(leftspike3, 0F, 0F, 0F);
		leftspike4 = new ModelRenderer(this, 0, 8);
		leftspike4.addBox(6.5F, -2F, 3F, 1, 2, 1);
		leftspike4.setRotationPoint(0F, 15F, 0F);
		leftspike4.setTextureSize(64, 32);
		leftspike4.mirror = true;
		setRotation(leftspike4, 0F, 0F, 0F);
		leftspike5 = new ModelRenderer(this, 0, 8);
		leftspike5.addBox(6.5F, -2F, 7F, 1, 2, 1);
		leftspike5.setRotationPoint(0F, 15F, 0F);
		leftspike5.setTextureSize(64, 32);
		leftspike5.mirror = true;
		setRotation(leftspike5, 0F, 0F, 0F);
		trigger = new ModelRenderer(this, 0, 0);
		trigger.addBox(-1.5F, 0F, -1.5F, 3, 1, 3);
		trigger.setRotationPoint(0F, 14F, 0F);
		trigger.setTextureSize(64, 32);
		trigger.mirror = true;
		setRotation(trigger, 0F, 0F, 0F);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void renderAll(boolean shut, int ticksSinceOpened) {
		float f5 = 0.0625F;

		float rot = 1.4F;

		if (!shut) {
			rot = Math.max(0.0f, 1.4f - (ticksSinceOpened / TileEntityBearTrap.OPENING_ANIMATION_TIME));
		}

		leftnearside.rotateAngleZ = -rot;
		lefttopside.rotateAngleZ = -rot;
		leftfarside.rotateAngleZ = -rot;
		leftspike1.rotateAngleZ = -rot;
		leftspike2.rotateAngleZ = -rot;
		leftspike3.rotateAngleZ = -rot;
		leftspike4.rotateAngleZ = -rot;
		leftspike5.rotateAngleZ = -rot;

		rightnearside.rotateAngleZ = rot;
		righttopside.rotateAngleZ = rot;
		rightfarside.rotateAngleZ = rot;
		rightspike1.rotateAngleZ = rot;
		rightspike2.rotateAngleZ = rot;
		rightspike3.rotateAngleZ = rot;
		rightspike4.rotateAngleZ = rot;

		middle.render(f5);

		trigger.render(f5);
		leftnearside.render(f5);
		lefttopside.render(f5);
		leftfarside.render(f5);
		leftspike2.render(f5);
		leftspike1.render(f5);
		leftspike3.render(f5);
		leftspike4.render(f5);
		leftspike5.render(f5);

		righttopside.render(f5);
		rightfarside.render(f5);
		rightnearside.render(f5);
		rightspike1.render(f5);
		rightspike2.render(f5);
		rightspike3.render(f5);
		rightspike4.render(f5);
	}
}