package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;

public class ModelFlag extends ModelBase {
	// fields
	ModelRenderer pole;

	public ModelFlag() {
		textureWidth = 32;
		textureHeight = 32;

		pole = new ModelRenderer(this, 0, 0);
		pole.addBox(-0.5F, 0F, -0.5F, 1, 16, 1);
		pole.setRotationPoint(0F, 0F, 0F);
		pole.setTextureSize(32, 32);
		pole.mirror = true;
		setRotation(pole, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {

		float f5 = 0.0625F;
		setRotationAngles(te, f);
		pole.render(f5);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntity te, float f) {}

}
