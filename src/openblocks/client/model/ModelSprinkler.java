package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntitySprinkler;

public class ModelSprinkler extends ModelBase {

	ModelRenderer side1;
	ModelRenderer side2;
	ModelRenderer end1;
	ModelRenderer sprayer;
	ModelRenderer end2;

	public ModelSprinkler() {
		textureWidth = 64;
		textureHeight = 32;

		side1 = new ModelRenderer(this, 26, 0);
		side1.addBox(-0.5F, 0F, -6F, 1, 1, 12);
		side1.setRotationPoint(-2F, 14F, 0F);
		side1.setTextureSize(64, 32);
		side1.mirror = true;
		setRotation(side1, 0F, 0F, 0F);
		side2 = new ModelRenderer(this, 25, 0);
		side2.addBox(-0.5F, 0F, -6F, 1, 1, 12);
		side2.setRotationPoint(2F, 14F, 0F);
		side2.setTextureSize(64, 32);
		side2.mirror = true;
		setRotation(side2, 0F, 0F, 0F);
		end1 = new ModelRenderer(this, 0, 26);
		end1.addBox(-3F, 0F, -7F, 6, 3, 1);
		end1.setRotationPoint(0F, 13F, 0F);
		end1.setTextureSize(64, 32);
		end1.mirror = true;
		setRotation(end1, 0F, 0F, 0F);
		sprayer = new ModelRenderer(this, 0, 0);
		sprayer.addBox(-0.5F, -2.3F, -6F, 1, 1, 12);
		sprayer.setRotationPoint(0F, 15.5F, 0F);
		sprayer.setTextureSize(64, 32);
		sprayer.mirror = true;
		setRotation(sprayer, 0F, 0F, 0F);
		end2 = new ModelRenderer(this, 0, 26);
		end2.addBox(-3F, 0F, 6F, 6, 3, 1);
		end2.setRotationPoint(0F, 13F, 0F);
		end2.setTextureSize(64, 32);
		end2.mirror = true;
		setRotation(end2, 0F, 0F, 0F);
	}

	public void render(TileEntitySprinkler sprinkler, float f) {

		float f5 = 0.0625F;
		setRotationAngles(sprinkler, f);
		side1.render(f5);
		side2.render(f5);
		end1.render(f5);
		end2.render(f5);

		sprayer.render(f5);
	}

	private static void setRotation(ModelRenderer model, float x, float y,
			float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(TileEntity te, float f) {
		TileEntitySprinkler sprinkler = (TileEntitySprinkler) te;

		sprayer.rotateAngleZ = sprinkler.getSprayAngle();
	}

}
