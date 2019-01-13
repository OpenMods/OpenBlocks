package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityCannon;

public class ModelCannon extends ModelBase {

	private final double[] keyframes = new double[] {
			0,
			0.5,
			1,
			0.9,
			0.8,
			0.7,
			0.6,
			0.5,
			0.4,
			0.3,
			0.2,
			0.1,
			0
	};

	private final ModelRenderer body;
	private final ModelRenderer shooter;
	private final ModelRenderer base;
	private final ModelRenderer wheel;

	private static final float deg30 = (float)Math.toRadians(30);
	private static final float deg180 = (float)Math.toRadians(180);

	public ModelCannon() {
		textureWidth = 64;
		textureHeight = 32;

		body = new ModelRenderer(this, 0, 0);
		body.addBox(-3F, -5F, -3F, 6, 6, 6);
		body.setRotationPoint(0F, 11F, 3F);
		body.setTextureSize(64, 32);
		body.mirror = true;
		setRotation(body, 0.3490659F, 0F, 0F);
		shooter = new ModelRenderer(this, 34, 0);
		shooter.addBox(-2F, -4F, 2F, 4, 4, 6);
		shooter.setRotationPoint(0F, 11F, 3F);
		shooter.setTextureSize(64, 32);
		shooter.mirror = true;
		setRotation(shooter, 0.3490659F, 0F, 0F);
		base = new ModelRenderer(this, 14, 19);
		base.addBox(-6F, 0F, -6F, 12, 1, 12);
		base.setRotationPoint(0F, 15F, 0F);
		base.setTextureSize(64, 32);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		wheel = new ModelRenderer(this, 0, 20);
		wheel.addBox(3F, -3F, -3F, 1, 6, 6);
		wheel.setRotationPoint(0F, 11F, 3F);
		wheel.setTextureSize(64, 32);
		wheel.mirror = true;
		setRotation(wheel, 0F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {

		TileEntityCannon cannon = (TileEntityCannon)te;

		float f5 = 0.0625F;

		int elapsed = Math.min(12, cannon != null? cannon.getTicksSinceLastFire() : 100);
		double ease = keyframes[elapsed];
		float shooterAnim = -(float)(3.0f * ease);
		shooter.rotateAngleX = (float)Math.toRadians(cannon != null? cannon.currentPitch : 45);
		float z = (float)(3 + shooterAnim * Math.cos(shooter.rotateAngleX));
		float y = (float)(11 - shooterAnim * Math.sin(shooter.rotateAngleX));
		shooter.rotationPointY = y;
		shooter.rotationPointZ = z;
		float cannonOffset = (float)ease * 4;
		body.rotationPointZ = 3f - cannonOffset;
		shooter.rotationPointZ -= cannonOffset;

		wheel.rotationPointZ = -cannonOffset;

		shooter.rotateAngleX += ease / 2;
		body.rotateAngleX = shooter.rotateAngleX;

		body.rotateAngleX = shooter.rotateAngleX;
		body.render(f5);
		shooter.render(f5);
		base.render(f5);

		float startAngleX = (float)ease;

		wheel.rotateAngleZ = 0;
		wheel.rotateAngleX = startAngleX;
		wheel.rotationPointX = 0;
		wheel.render(f5);
		wheel.rotateAngleX += deg30;
		wheel.rotationPointX -= 0.01;
		wheel.render(f5);
		wheel.rotateAngleX += deg30;
		wheel.rotationPointX -= 0.01;
		wheel.render(f5);

		wheel.rotateAngleZ = deg180;
		wheel.rotateAngleX = -startAngleX;
		wheel.rotationPointX = 0;
		wheel.render(f5);
		wheel.rotateAngleX += deg30;
		wheel.rotationPointX -= 0.01;
		wheel.render(f5);
		wheel.rotateAngleX += deg30;
		wheel.rotationPointX -= 0.01;
		wheel.render(f5);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
