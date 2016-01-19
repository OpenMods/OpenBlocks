package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import openblocks.common.tileentity.TileEntityGrave;
import openmods.renderer.ITileEntityModel;

public class ModelGrave extends ModelBase implements ITileEntityModel<TileEntityGrave> {

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

	@Override
	public void render(TileEntityGrave te, float partialTicks) {
		if (te == null || te.isOnSoil()) {
			stone.setRotationPoint(0F, 15.5F, 6F);
			floor.render(0.0625F);
		} else {
			stone.setRotationPoint(0F, 16.5F, 6F);
		}
		stone.render(0.0625F);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
