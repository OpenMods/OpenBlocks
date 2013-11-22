package openblocks.client.renderer.mutant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import openblocks.api.IMutant;
import openblocks.api.IMutantRenderer;
import openblocks.utils.MutantUtils;

public class MutantRendererEnderman implements IMutantRenderer {

	private static final ResourceLocation endermanEyesTexture = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	private static final ResourceLocation texture = new ResourceLocation("textures/entity/enderman/enderman.png");

	private ModelRenderer head;
	private ModelRenderer headwear;
	private ModelRenderer body;
	private ModelRenderer leftLeg;
	private ModelRenderer rightLeg;
	private ModelRenderer leftArm;
	private ModelRenderer rightArm;

	@Override
	public void initialize(ModelBase base) {
		head = new ModelRenderer(base, 0, 0);
		head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);

		headwear = new ModelRenderer(base, 0, 16);
		headwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, -0.5F);

		rightLeg = new ModelRenderer(base, 56, 0);
		rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2);

		leftLeg = new ModelRenderer(base, 56, 0);
		leftLeg.mirror = true;
		leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2);

		rightArm = new ModelRenderer(base, 56, 0);
		rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2);
		leftArm = new ModelRenderer(base, 56, 0);
		leftArm.mirror = true;
		leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2);

		body = new ModelRenderer(base, 32, 16);
		body.addBox(-4.0F, -0.0F, -2.0F, 8, 12, 4);
	}

	@Override
	public void renderHead(IMutant mutant, float scale, float yaw, float pitch) {

		head.showModel = true;
		Vec3 headPoint = mutant.getBody().getHeadAttachmentPoint();
		MutantUtils.bindToAttachmentPoint(mutant, head, headPoint);
		MutantUtils.bindToAttachmentPoint(mutant, headwear, headPoint);
		bindTexture();
		head.rotateAngleX = pitch / (180F / (float)Math.PI);
		head.rotateAngleY = yaw / (180F / (float)Math.PI);
		headwear.rotateAngleY = head.rotateAngleY;
		headwear.rotateAngleX = head.rotateAngleX;
		head.render(scale);
		Minecraft.getMinecraft().renderEngine.bindTexture(endermanEyesTexture);
		headwear.render(scale);
	}

	@Override
	public void renderLegs(IMutant mutant, float scale, float legSwing, float prevLegSwing) {

		bindTexture();

		Vec3[] legAttachmentPoints = mutant.getBody().getLegAttachmentPoints(mutant.getLegs().getNumberOfLegs());

		MutantUtils.bindToAttachmentPoint(mutant, leftLeg, legAttachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, rightLeg, legAttachmentPoints[1]);

		rightLeg.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 1.4F * prevLegSwing;
		leftLeg.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float)Math.PI) * 1.4F * prevLegSwing;
		rightLeg.rotateAngleY = 0.0F;
		leftLeg.rotateAngleY = 0.0F;

		rightLeg.rotateAngleX = rightLeg.rotateAngleX * 0.5f;
		leftLeg.rotateAngleX = leftLeg.rotateAngleX * 0.5f;
		float var9 = 0.4F;
		if (rightLeg.rotateAngleX > var9) {
			rightLeg.rotateAngleX = var9;
		}

		if (leftLeg.rotateAngleX > var9) {
			leftLeg.rotateAngleX = var9;
		}

		if (rightLeg.rotateAngleX < -var9) {
			rightLeg.rotateAngleX = -var9;
		}

		if (leftLeg.rotateAngleX < -var9) {
			leftLeg.rotateAngleX = -var9;
		}
		leftLeg.render(scale);
		rightLeg.render(scale);
	}

	@Override
	public void renderBody(IMutant mutant, float scale) {
		body.setRotationPoint(0, (float)24 - mutant.getLegHeight() - mutant.getBodyHeight(), 0);
		bindTexture();
		body.render(scale);
	}

	@Override
	public void renderWings(IMutant mutant, float scale, float wingSwing) {}

	@Override
	public void renderArms(IMutant mutant, float scale, float legSwing, float prevLegSwing) {
		bindTexture();

		Vec3[] armAttachmentPoints = mutant.getBody().getArmAttachmentPoints();

		MutantUtils.bindToAttachmentPoint(mutant, leftArm, armAttachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, rightArm, armAttachmentPoints[1]);

		rightArm.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float)Math.PI) * 2.0F * prevLegSwing * 0.5F;
		leftArm.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 2.0F * prevLegSwing * 0.5F;

		rightArm.rotateAngleX = rightArm.rotateAngleX * 0.5f;
		leftArm.rotateAngleX = leftArm.rotateAngleX * 0.5f;
		float var9 = 0.4F;

		if (rightArm.rotateAngleX > var9) {
			rightArm.rotateAngleX = var9;
		}

		if (leftArm.rotateAngleX > var9) {
			leftArm.rotateAngleX = var9;
		}

		if (rightArm.rotateAngleX < -var9) {
			rightArm.rotateAngleX = -var9;
		}

		if (leftArm.rotateAngleX < -var9) {
			leftArm.rotateAngleX = -var9;
		}
		leftArm.render(scale);
		rightArm.render(scale);
	}

	@Override
	public void renderTail(IMutant mutant, float scale, float legSwing, float prevLegSwing) {}

	private void bindTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}
}
