package openblocks.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityGrave;

public class ModelGrave extends ModelBase {

	
	ModelRenderer stone;
	ModelRenderer floor;

	public ModelGrave() {
		textureWidth = 128;
		textureHeight = 32;

		stone = new ModelRenderer(this, 64, 0);
		stone.addBox(-6F, -15F, -1F, 12, 15, 2);
		stone.setRotationPoint(0F, 15.5F, 6F);
		stone.setTextureSize(128, 32);
		stone.mirror = true;
		setRotation(stone, -0.0743572F, 0F, 0.0371786F);
		floor = new ModelRenderer(this, 0, 0);
		floor.addBox(-8F, 0F, -8F, 16, 1, 16);
		floor.setRotationPoint(0F, 15F, 0F);
		floor.setTextureSize(128, 32);
		floor.mirror = true;
		setRotation(floor, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {
		if(!(te instanceof TileEntityGrave)) return;
		float f5 = 0.0625F;
		setRotationAngles(te, f);
		stone.render(f5);
		if(((TileEntityGrave)te).onSoil) floor.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntity te, float f) {
	}

}
