package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityTarget;

public class ModelTarget extends AbstractModel {

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

	public void render(TileEntity te, float f) {

		setRotationAngles(te, f);
		stand1.render(SCALE);
		target.render(SCALE);
		stand2.render(SCALE);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	/**
	 * @param f
	 */
	public void setRotationAngles(TileEntity te, float f) {
		TileEntityTarget targetTe = (TileEntityTarget)te;
		target.rotateAngleX = targetTe.getTargetRotation();
	}

}
