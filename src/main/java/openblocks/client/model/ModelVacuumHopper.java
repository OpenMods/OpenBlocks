package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.sync.SyncableFlags;

public class ModelVacuumHopper extends ModelBase {

	ModelRenderer middle;
	ModelRenderer outputItems;
	ModelRenderer outputXP;
	ModelRenderer output2;
	ModelRenderer outputBoth;

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

		outputBoth = new ModelRenderer(this, 0, 16);
		outputBoth.addBox(-1.5F, -8F, -1.5F, 3, 3, 3);
		outputBoth.setRotationPoint(0F, 8F, 0F);
		outputBoth.setTextureSize(64, 32);
		outputBoth.mirror = true;
		setRotation(outputBoth, 0F, 0F, 0F);

		outputXP = new ModelRenderer(this, 12, 16);
		outputXP.addBox(-1.5F, -8F, -1.5F, 3, 3, 3);
		outputXP.setRotationPoint(0F, 8F, 0F);
		outputXP.setTextureSize(64, 32);
		outputXP.mirror = true;
		setRotation(outputXP, 0F, 0F, 0F);

		outputItems = new ModelRenderer(this, 24, 16);
		outputItems.addBox(-1.5F, -8F, -1.5F, 3, 3, 3);
		outputItems.setRotationPoint(0F, 8F, 0F);
		outputItems.setTextureSize(64, 32);
		outputItems.mirror = true;
		setRotation(outputItems, 0F, 0F, 0F);
	}

	private void renderValve(SyncableFlags itemOutputs, SyncableFlags xpOutputs, ForgeDirection direction, float rotX, float rotZ, float f5) {

		boolean items = itemOutputs.get(direction);
		boolean xp = xpOutputs.get(direction);

		if (items || xp) {
			ModelRenderer valve = items && xp? outputBoth : items? outputItems : outputXP;
			output2.rotateAngleX = valve.rotateAngleX = rotX;
			output2.rotateAngleZ = valve.rotateAngleZ = rotZ;
			output2.render(f5);
			valve.render(f5);
		}
	}

	/**
	 * @param f
	 */
	public void render(TileEntityVacuumHopper hopper, float f) {

		float f5 = 0.0625F;
		middle.render(f5);
		SyncableFlags itemOutputs = hopper.getItemOutputs();
		SyncableFlags xpOutputs = hopper.getXPOutputs();
		renderValve(itemOutputs, xpOutputs, ForgeDirection.UP, 0, 0, f5);
		renderValve(itemOutputs, xpOutputs, ForgeDirection.DOWN, (float)Math.toRadians(180), 0, f5);
		renderValve(itemOutputs, xpOutputs, ForgeDirection.EAST, 0, (float)Math.toRadians(90), f5);
		renderValve(itemOutputs, xpOutputs, ForgeDirection.WEST, 0, (float)Math.toRadians(-90), f5);
		renderValve(itemOutputs, xpOutputs, ForgeDirection.NORTH, (float)Math.toRadians(-90), 0, f5);
		renderValve(itemOutputs, xpOutputs, ForgeDirection.SOUTH, (float)Math.toRadians(90), 0, f5);

	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
