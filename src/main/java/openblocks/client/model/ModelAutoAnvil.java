package openblocks.client.model;

import net.minecraft.client.model.ModelRenderer;

public class ModelAutoAnvil extends AbstractModel {
	// fields
	ModelRenderer level1;
	ModelRenderer level2;
	ModelRenderer level3;
	ModelRenderer level4;

	public ModelAutoAnvil() {
		textureWidth = 128;
		textureHeight = 32;
		level1 = new ModelRenderer(this, 0, 0);
		level1.addBox(-8F, 0F, -5F, 16, 6, 10);
		level1.setRotationPoint(0F, 0F, 0F);
		level1.setTextureSize(128, 32);
		level1.mirror = true;
		setRotation(level1, 0F, 0F, 0F);
		level2 = new ModelRenderer(this, 0, 16);
		level2.addBox(-4F, 6F, -2F, 8, 5, 4);
		level2.setRotationPoint(0F, 0F, 0F);
		level2.setTextureSize(128, 32);
		level2.mirror = true;
		setRotation(level2, 0F, 0F, 0F);
		level3 = new ModelRenderer(this, 24, 16);
		level3.addBox(-5F, 11F, -4F, 10, 1, 8);
		level3.setRotationPoint(0F, 0F, 0F);
		level3.setTextureSize(128, 32);
		level3.mirror = true;
		setRotation(level3, 0F, 0F, 0F);
		level4 = new ModelRenderer(this, 52, 0);
		level4.addBox(-7F, 12F, -6F, 14, 4, 12);
		level4.setRotationPoint(0F, 0F, 0F);
		level4.setTextureSize(128, 32);
		level4.mirror = true;
		setRotation(level4, 0F, 0F, 0F);
	}

	public void render() {
		level1.render(SCALE);
		level2.render(SCALE);
		level3.render(SCALE);
		level4.render(SCALE);
	}

}