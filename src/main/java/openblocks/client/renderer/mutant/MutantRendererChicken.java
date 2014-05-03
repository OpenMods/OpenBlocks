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

public class MutantRendererChicken implements IMutantRenderer {

	private static final ResourceLocation texture = new ResourceLocation("textures/entity/chicken.png");

	private ModelRenderer head;
	private ModelRenderer bill;
	private ModelRenderer chin;

	private ModelRenderer body;
	private ModelRenderer rightLeg;
	private ModelRenderer leftLeg;
	private ModelRenderer rightWing;
	private ModelRenderer leftWing;

	@Override
	public void initialize(ModelBase base) {
		head = new ModelRenderer(base, 0, 0);
		head.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3);
		bill = new ModelRenderer(base, 14, 0);
		bill.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2);
		chin = new ModelRenderer(base, 14, 4);
		chin.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2);

		rightLeg = new ModelRenderer(base, 26, 0);
		rightLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		leftLeg = new ModelRenderer(base, 26, 0);
		leftLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);

		body = new ModelRenderer(base, 0, 9);
		body.addBox(-3.0F, -4.0F, -6.0F, 6, 8, 6);

		rightWing = new ModelRenderer(base, 24, 13);
		rightWing.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6);
		leftWing = new ModelRenderer(base, 24, 13);
		leftWing.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6);
	}

	@Override
	public void renderHead(IMutant mutant, float scale, float yaw, float pitch) {
		bindTexture();

		Vec3 headAttachmentPoint = mutant.getBody().getHeadAttachmentPoint();
		MutantUtils.bindToAttachmentPoint(mutant, head, headAttachmentPoint);
		MutantUtils.bindToAttachmentPoint(mutant, bill, headAttachmentPoint);
		MutantUtils.bindToAttachmentPoint(mutant, chin, headAttachmentPoint);

		head.rotateAngleX = pitch / (180F / (float)Math.PI);
		head.rotateAngleY = yaw / (180F / (float)Math.PI);
		bill.rotateAngleX = head.rotateAngleX;
		bill.rotateAngleY = head.rotateAngleY;
		chin.rotateAngleX = head.rotateAngleX;
		chin.rotateAngleY = head.rotateAngleY;
		head.render(scale);
		bill.render(scale);
		chin.render(scale);
	}

	@Override
	public void renderLegs(IMutant mutant, float scale, float legSwing, float prevLegSwing) {
		bindTexture();
		Vec3[] legAttachmentPoints = mutant.getBody().getLegAttachmentPoints(mutant.getLegs().getNumberOfLegs());
		MutantUtils.bindToAttachmentPoint(mutant, leftLeg, legAttachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, rightLeg, legAttachmentPoints[1]);

		rightLeg.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 1.4F * prevLegSwing;
		leftLeg.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float)Math.PI) * 1.4F * prevLegSwing;

		leftLeg.render(scale);
		rightLeg.render(scale);
	}

	@Override
	public void renderBody(IMutant mutant, float scale) {
		body.setRotationPoint(0, (float)24 - mutant.getLegHeight() - mutant.getBodyHeight(), 0);
		body.rotateAngleX = ((float)Math.PI / 2F);
		bindTexture();
		body.render(scale);
	}

	@Override
	public void renderWings(IMutant mutant, float scale, float wingSwing) {
		bindTexture();

		Vec3[] wingAttachmentPoints = mutant.getBody().getWingAttachmentPoints();
		MutantUtils.bindToAttachmentPoint(mutant, leftWing, wingAttachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, rightWing, wingAttachmentPoints[1]);

		rightWing.rotateAngleZ = -wingSwing;
		leftWing.rotateAngleZ = wingSwing;
		leftWing.render(scale);
		rightWing.render(scale);
	}

	@Override
	public void renderArms(IMutant mutant, float scale, float legSwing, float prevLegSwing) {}

	@Override
	public void renderTail(IMutant mutant, float scale, float legSwing, float prevLegSwing) {}

	private static void bindTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}
}
