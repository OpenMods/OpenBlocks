package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import openblocks.common.tileentity.TileEntityTarget;
import openmods.renderer.ITileEntityModel;

public class ModelTarget extends ModelBase implements ITileEntityModel<TileEntityTarget> {

	ModelRenderer stand1;
	ModelRenderer target;
	ModelRenderer stand2;

	public ModelTarget() {
		textureWidth = 64;
		textureHeight = 32;

		stand1 = new ModelRenderer(this, 0, 16);
		stand1.addBox(-8F, 0F, 7F, 16, 1, 1);
		stand1.setRotationPoint(0F, 15F, 0F);
		stand1.setTextureSize(64, 32);
		stand1.mirror = true;
		setRotation(stand1, 0F, 1.570796F, 0F);
		target = new ModelRenderer(this, 0, 0);
		target.addBox(-8F, -15F, -1F, 16, 15, 1);
		target.setRotationPoint(0F, 15F, -7F);
		target.setTextureSize(64, 32);
		target.mirror = true;
		setRotation(target, 0F, 0F, 0F);
		stand2 = new ModelRenderer(this, 0, 16);
		stand2.addBox(-8F, 0F, -8F, 16, 1, 1);
		stand2.setRotationPoint(0F, 15F, 0F);
		stand2.setTextureSize(64, 32);
		stand2.mirror = true;
		setRotation(stand2, 0F, 1.570796F, 0F);
	}

	@Override
	public void render(TileEntityTarget te, float f) {
		target.rotateAngleX = te != null? te.getTargetRotation() : 0;

		stand1.render(0.0625F);
		target.render(0.0625F);
		stand2.render(0.0625F);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
