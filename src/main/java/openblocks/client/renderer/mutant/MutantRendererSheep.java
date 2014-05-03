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

public class MutantRendererSheep implements IMutantRenderer {

	private static final ResourceLocation texture = new ResourceLocation("textures/entity/sheep/sheep.png");
	private static final ResourceLocation textureFur = new ResourceLocation("textures/entity/sheep/sheep_fur.png");

	private ModelRenderer head;
	private ModelRenderer head2;
	private ModelRenderer body;
	private ModelRenderer body2;
	private ModelRenderer leg1;
	private ModelRenderer leg2;
	private ModelRenderer leg3;
	private ModelRenderer leg4;

	private ModelRenderer leg1_2;
	private ModelRenderer leg2_2;
	private ModelRenderer leg3_2;
	private ModelRenderer leg4_2;

	@Override
	public void initialize(ModelBase base) {

		head = new ModelRenderer(base, 0, 0);
		head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);

		head2 = new ModelRenderer(base, 0, 0);
		head2.addBox(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);

		body = new ModelRenderer(base, 28, 8);
		body.addBox(-4.0F, -8.0F, -6.0F, 8, 16, 6);

		body2 = new ModelRenderer(base, 28, 8);
		body2.addBox(-4.0F, -8.0F, -6.0F, 8, 16, 6, 1.75F);

		leg1 = new ModelRenderer(base, 0, 16);
		leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
		leg2 = new ModelRenderer(base, 0, 16);
		leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
		leg3 = new ModelRenderer(base, 0, 16);
		leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
		leg4 = new ModelRenderer(base, 0, 16);
		leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);

		leg1_2 = new ModelRenderer(base, 0, 16);
		leg1_2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		leg2_2 = new ModelRenderer(base, 0, 16);
		leg2_2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		leg3_2 = new ModelRenderer(base, 0, 16);
		leg3_2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		leg4_2 = new ModelRenderer(base, 0, 16);
		leg4_2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
	}

	@Override
	public void renderHead(IMutant mutant, float scale, float yaw, float pitch) {
		bindTexture(texture);
		MutantUtils.bindToAttachmentPoint(mutant, head, mutant.getBody().getHeadAttachmentPoint());
		head.rotateAngleX = pitch / (180F / (float)Math.PI);
		head.rotateAngleY = yaw / (180F / (float)Math.PI);
		head.render(scale);
		bindTexture(textureFur);
		MutantUtils.bindToAttachmentPoint(mutant, head2, mutant.getBody().getHeadAttachmentPoint());
		head2.rotateAngleX = head.rotateAngleX;
		head2.rotateAngleY = head.rotateAngleY;
		head2.render(scale);
	}

	@Override
	public void renderLegs(IMutant mutant, float scale, float legSwing, float prevLegSwing) {

		leg1_2.rotationPointX = leg1.rotationPointX;
		leg1_2.rotationPointY = leg1.rotationPointY;
		leg1_2.rotationPointZ = leg1.rotationPointZ;

		leg2_2.rotationPointX = leg2.rotationPointX;
		leg2_2.rotationPointY = leg2.rotationPointY;
		leg2_2.rotationPointZ = leg2.rotationPointZ;

		leg3_2.rotationPointX = leg3.rotationPointX;
		leg3_2.rotationPointY = leg3.rotationPointY;
		leg3_2.rotationPointZ = leg3.rotationPointZ;

		leg4_2.rotationPointX = leg4.rotationPointX;
		leg4_2.rotationPointY = leg4.rotationPointY;
		leg4_2.rotationPointZ = leg4.rotationPointZ;

		leg1.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 1.4F * prevLegSwing;
		leg2.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float)Math.PI) * 1.4F * prevLegSwing;
		leg3.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float)Math.PI) * 1.4F * prevLegSwing;
		leg4.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 1.4F * prevLegSwing;
		leg1_2.rotateAngleX = leg1.rotateAngleX;
		leg2_2.rotateAngleX = leg2.rotateAngleX;
		leg3_2.rotateAngleX = leg3.rotateAngleX;
		leg4_2.rotateAngleX = leg4.rotateAngleX;
		bindTexture(texture);
		Vec3[] attachmentPoints = mutant.getBody().getLegAttachmentPoints(mutant.getLegs().getNumberOfLegs());
		MutantUtils.bindToAttachmentPoint(mutant, leg1, attachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, leg2, attachmentPoints[1]);
		MutantUtils.bindToAttachmentPoint(mutant, leg3, attachmentPoints[2]);
		MutantUtils.bindToAttachmentPoint(mutant, leg4, attachmentPoints[3]);
		MutantUtils.bindToAttachmentPoint(mutant, leg1_2, attachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, leg2_2, attachmentPoints[1]);
		MutantUtils.bindToAttachmentPoint(mutant, leg3_2, attachmentPoints[2]);
		MutantUtils.bindToAttachmentPoint(mutant, leg4_2, attachmentPoints[3]);
		leg1.render(scale);
		leg2.render(scale);
		leg3.render(scale);
		leg4.render(scale);
		bindTexture(textureFur);
		leg1_2.render(scale);
		leg2_2.render(scale);
		leg3_2.render(scale);
		leg4_2.render(scale);
	}

	@Override
	public void renderBody(IMutant mutant, float scale) {
		body.setRotationPoint(0, (float)24 - mutant.getLegHeight() - mutant.getBodyHeight(), 1.0F);
		bindTexture(texture);
		body.rotateAngleX = ((float)Math.PI / 2F);
		body.render(scale);
		bindTexture(textureFur);
		body2.rotationPointX = body.rotationPointX;
		body2.rotationPointY = body.rotationPointY;
		body2.rotationPointZ = body.rotationPointZ;
		body2.rotateAngleX = body.rotateAngleX;
		body2.render(scale);// TODO Auto-generated method stub

	}

	@Override
	public void renderWings(IMutant mutant, float scale, float wingSwing) {}

	@Override
	public void renderArms(IMutant mutant, float scale, float legSwing, float prevLegSwing) {}

	@Override
	public void renderTail(IMutant mutant, float scale, float legSwing, float prevLegSwing) {}

	private static void bindTexture(ResourceLocation loc) {
		Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}
}
