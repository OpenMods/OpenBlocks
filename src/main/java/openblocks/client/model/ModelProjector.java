package openblocks.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelProjector extends AbstractModel {

	private static final float DEG_45 = (float)Math.toRadians(45);
	// private static final float DEG_135 = (float)Math.toRadians(135);

	private final ModelRenderer base1;
	private final ModelRenderer base2;
	private final ModelRenderer arms[] = new ModelRenderer[4];

	private void makeOuterArm(float posX, float posZ, float angleY, int index) {
		ModelRenderer arm = new ModelRenderer(this, 4 * index, 14);
		arm.addBox(-0.5f, -0.5f, -0.5f, 1, 5, 1);
		arm.setRotationPoint(posX, 1F, posZ);
		arm.rotateAngleY = angleY;
		base1.addChild(arm);
		arms[index] = arm;
	}

	public ModelProjector() {
		textureWidth = 32;
		textureHeight = 32;

		base1 = new ModelRenderer(this, 0, 0);
		base1.addBox(-4f, 0f, -4F, 8, 1, 8);
		base1.setRotationPoint(4f, 0F, 4f);

		makeOuterArm(-3, -3, +DEG_45, 0);
		makeOuterArm(-3, +3, -DEG_45, 1);
		makeOuterArm(+3, +3, +DEG_45, 2);
		makeOuterArm(+3, -3, -DEG_45, 3);

		base2 = new ModelRenderer(this);
		base2.setRotationPoint(4f, 0, 4f);

		base2.setTextureOffset(0, 9);
		base2.addBox(-2, +1, -2, 4, 1, 4);

		base2.setTextureOffset(0, 20);
		base2.addBox(-2, +2, -2, 1, 2, 1);

		base2.setTextureOffset(4, 20);
		base2.addBox(+1, +2, -2, 1, 2, 1);

		base2.setTextureOffset(8, 20);
		base2.addBox(+1, +2, +1, 1, 2, 1);

		base2.setTextureOffset(12, 20);
		base2.addBox(-2, +2, +1, 1, 2, 1);
	}

	public void render(float rotationBase1, float rotationBase2, float armRotation) {
		base1.rotateAngleY = rotationBase1;
		base2.rotateAngleY = rotationBase2;

		arms[0].rotateAngleX = arms[3].rotateAngleX = -armRotation;
		arms[1].rotateAngleX = arms[2].rotateAngleX = +armRotation;

		base1.render(SCALE);
		base2.render(SCALE);
	}

}
