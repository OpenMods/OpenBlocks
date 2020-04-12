package openblocks.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSleepingBag extends ModelBiped {

	public static final ModelSleepingBag instance = new ModelSleepingBag();

	private final ModelRenderer main;
	private final ModelRenderer pillow;

	public ModelSleepingBag() {

		textureWidth = 128;
		textureHeight = 64;

		main = new ModelRenderer(this, 0, 0);
		main.addBox(-9F, 0F, -3F, 18, 26, 7);
		main.setRotationPoint(0F, 0F, 0F);
		main.setTextureSize(128, 64);
		main.mirror = true;
		setRotation(main, 0F, 0F, 0F);
		pillow = new ModelRenderer(this, 50, 0);
		pillow.addBox(-8F, -9F, 0F, 8, 18, 1);
		pillow.setRotationPoint(0F, 0F, 3F);
		pillow.setTextureSize(128, 64);
		pillow.mirror = true;
		setRotation(pillow, 0F, 0F, (float)(Math.PI / 2));
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void render(Entity entity, float swingTime, float swingAmpl, float rightArmAngle, float headAngleX, float headAngleY, float scale) {
		main.render(scale);
		pillow.render(scale);
	}

}
