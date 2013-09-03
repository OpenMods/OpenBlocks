package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityVacuumHopper;

public class ModelVacuumHopper extends ModelBase {

	// fields
	ModelRenderer connector;
	ModelRenderer collector;

	public ModelVacuumHopper()
	{
		textureWidth = 64;
		textureHeight = 32;

		connector = new ModelRenderer(this, 0, 12);
		connector.addBox(-1F, 0F, 3F, 2, 2, 5);
		connector.setRotationPoint(0F, 8F, 0F);
		connector.setTextureSize(64, 32);
		connector.mirror = true;
		setRotation(connector, 0F, 0F, 0F);
		collector = new ModelRenderer(this, 0, 0);
		collector.addBox(-3F, 0F, -3F, 6, 6, 6);
		collector.setRotationPoint(0F, 6F, 0F);
		collector.setTextureSize(64, 32);
		collector.mirror = true;
		setRotation(collector, 0F, 0F, 0F);
	}

	public void render(TileEntityVacuumHopper hopper, float f) {

		float f5 = 0.0625F;
		setRotationAngles(hopper, f);
		collector.render(f5);
		connector.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntity te, float f) {}

}
