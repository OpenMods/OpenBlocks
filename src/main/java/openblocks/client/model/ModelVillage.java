package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;

public class ModelVillage extends AbstractModel {
	// fields
	ModelRenderer main;
	ModelRenderer step;
	ModelRenderer roof;

	public ModelVillage() {
		textureWidth = 128;
		textureHeight = 64;

		main = new ModelRenderer(this, 0, 0);
		main.addBox(-8F, 0F, -6F, 16, 14, 14);
		main.setRotationPoint(0F, 2F, 0F);
		main.setTextureSize(128, 64);
		main.mirror = true;
		setRotation(main, 0F, 0F, 0F);
		step = new ModelRenderer(this, 0, 40);
		step.addBox(-2F, 0F, 0F, 4, 1, 2);
		step.setRotationPoint(0F, 15F, -8F);
		step.setTextureSize(128, 64);
		step.mirror = true;
		setRotation(step, 0F, 0F, 0F);
		roof = new ModelRenderer(this, 0, 28);
		roof.addBox(-6F, 0F, -4F, 12, 2, 10);
		roof.setRotationPoint(0F, 0F, 0F);
		roof.setTextureSize(128, 64);
		roof.mirror = true;
		setRotation(roof, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {

		setRotationAngles(te, f);
		main.render(SCALE);
		step.render(SCALE);
		roof.render(SCALE);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	/**
	 * @param te
	 * @param f
	 */
	public void setRotationAngles(TileEntity te, float f) {}

}
