package openblocks.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityGrave;

public class ModelGrave extends AbstractModel {

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
		if (!(te instanceof TileEntityGrave)) return;
		setRotationAngles(te, f);
		if (((TileEntityGrave)te).isOnSoil()) {
			stone.setRotationPoint(0F, 15.5F, 6F);
			floor.render(SCALE);
		} else {
			stone.setRotationPoint(0F, 16.5F, 6F);
		}
		stone.render(SCALE);
	}

	/**
	 * @param te
	 * @param f
	 */
	public void setRotationAngles(TileEntity te, float f) {}

}
