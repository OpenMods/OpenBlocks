package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.sync.SyncableFlags;

public class ModelVacuumHopper extends ModelBase {

	ModelRenderer middle;
	ModelRenderer output2;
	ModelRenderer output;

	public ModelVacuumHopper() {
		textureWidth = 64;
		textureHeight = 32;

		middle = new ModelRenderer(this, 0, 0);
		middle.addBox(-4F, -4F, -4F, 8, 8, 8);
		middle.setRotationPoint(0F, 8F, 0F);
		middle.setTextureSize(64, 32);
		middle.mirror = true;
		setRotation(middle, 0F, 0F, 0F);
		output2 = new ModelRenderer(this, 0, 22);
		output2.addBox(-2.5F, -5F, -2.5F, 5, 1, 5);
		output2.setRotationPoint(0F, 8F, 0F);
		output2.setTextureSize(64, 32);
		output2.mirror = true;
		setRotation(output2, 0F, 0F, 0F);
		output = new ModelRenderer(this, 0, 16);
		output.addBox(-1.5F, -8F, -1.5F, 3, 3, 3);
		output.setRotationPoint(0F, 8F, 0F);
		output.setTextureSize(64, 32);
		output.mirror = true;
		setRotation(output, 0F, 0F, 0F);
	}

	public void render(TileEntityVacuumHopper hopper, float f) {

		float f5 = 0.0625F;
		middle.render(f5);
		SyncableFlags itemOutputs = hopper.getItemOutputs();
		SyncableFlags xpOutputs = hopper.getXPOutputs();
		if (itemOutputs.get(ForgeDirection.UP) || xpOutputs.get(ForgeDirection.UP)) {
			output.rotateAngleX = output2.rotateAngleX = 0;
			output.rotateAngleZ = output2.rotateAngleZ = 0;
			output.render(f5);
			output2.render(f5);
		}
		if (itemOutputs.get(ForgeDirection.DOWN) || xpOutputs.get(ForgeDirection.DOWN)) {
			output.rotateAngleX = output2.rotateAngleX = (float)Math.toRadians(180);
			output.rotateAngleZ = output2.rotateAngleZ = 0;
			output.render(f5);
			output2.render(f5);
		}
		if (itemOutputs.get(ForgeDirection.EAST) || xpOutputs.get(ForgeDirection.EAST)) {
			output.rotateAngleX = output2.rotateAngleX = 0;
			output.rotateAngleZ = output2.rotateAngleZ = (float)Math.toRadians(90);
			output.render(f5);
			output2.render(f5);
		}
		if (itemOutputs.get(ForgeDirection.WEST) || xpOutputs.get(ForgeDirection.WEST)) {
			output.rotateAngleX = output2.rotateAngleX = 0;
			output.rotateAngleZ = output2.rotateAngleZ = (float)Math.toRadians(-90);
			output.render(f5);
			output2.render(f5);
		}
		if (itemOutputs.get(ForgeDirection.NORTH) || xpOutputs.get(ForgeDirection.NORTH)) {
			output.rotateAngleX = output2.rotateAngleX = (float)Math.toRadians(-90);
			output.rotateAngleZ = output2.rotateAngleZ = 0;
			output.render(f5);
			output2.render(f5);
		}
		if (itemOutputs.get(ForgeDirection.SOUTH) || xpOutputs.get(ForgeDirection.SOUTH)) {
			output.rotateAngleX = output2.rotateAngleX = (float)Math.toRadians(90);
			output.rotateAngleZ = output2.rotateAngleZ = 0;
			output.render(f5);
			output2.render(f5);
		}

	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
