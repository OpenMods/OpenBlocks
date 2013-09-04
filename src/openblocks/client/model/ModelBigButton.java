package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityBigButton;
import openblocks.common.tileentity.TileEntityVacuumHopper;

public class ModelBigButton extends ModelBase {
	// fields
	ModelRenderer button;

	public ModelBigButton()
	{
		textureWidth = 64;
		textureHeight = 32;

		button = new ModelRenderer(this, 0, 0);
		button.addBox(-7F, -7F, 6F, 14, 14, 2);
		button.setRotationPoint(0F, 8F, 0F);
		button.setTextureSize(64, 32);
		button.mirror = true;
		setRotation(button, 0F, 0F, 0F);
	}

	public void render(TileEntityBigButton te, float f) {
		float f5 = 0.0625F;
		setRotationAngles(te, f);
		if (te.getFlag1()) {
			button.rotationPointZ = 1f;
		}else {
			button.rotationPointZ = 0;
		}
		button.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntityBigButton te, float f) {
		
		
	}

}
