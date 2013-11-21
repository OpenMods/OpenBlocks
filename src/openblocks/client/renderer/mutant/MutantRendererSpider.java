package openblocks.client.renderer.mutant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import openblocks.api.IMutant;
import openblocks.api.IMutantRenderer;
import openmods.utils.MutantUtils;

public class MutantRendererSpider implements IMutantRenderer {

	private static final ResourceLocation spiderEyesTextures = new ResourceLocation("textures/entity/spider_eyes.png");
	private static final ResourceLocation texture = new ResourceLocation("textures/entity/spider/spider.png");

	public ModelRenderer head;
	public ModelRenderer neck;
	public ModelRenderer body;
	public ModelRenderer leg1;
	public ModelRenderer leg2;
	public ModelRenderer leg3;
	public ModelRenderer leg4;
	public ModelRenderer leg5;
	public ModelRenderer leg6;
	public ModelRenderer leg7;
	public ModelRenderer leg8;

	@Override
	public void initialize(ModelBase base) {

		base.textureWidth = 64;
		base.textureHeight = 32;

		head = new ModelRenderer(base, 32, 4);
		head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8);

		neck = new ModelRenderer(base, 0, 0);
		neck.addBox(-3.0F, 3.0F, 3.0F, 6, 6, 6);

		body = new ModelRenderer(base, 0, 12);
		body.addBox(-5.0F, 0, -6.0F, 10, 8, 12);

		leg1 = new ModelRenderer(base, 18, 0);
		leg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2);
		leg2 = new ModelRenderer(base, 18, 0);
		leg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2);
		leg3 = new ModelRenderer(base, 18, 0);
		leg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2);
		leg4 = new ModelRenderer(base, 18, 0);
		leg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2);
		leg5 = new ModelRenderer(base, 18, 0);
		leg5.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2);
		leg6 = new ModelRenderer(base, 18, 0);
		leg6.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2);
		leg7 = new ModelRenderer(base, 18, 0);
		leg7.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2);
		leg8 = new ModelRenderer(base, 18, 0);
		leg8.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2);
	}

	@Override
	public void renderHead(IMutant mutant, float scale, float yaw, float pitch) {
		bindTexture();

		MutantUtils.bindToAttachmentPoint(mutant, head, mutant.getBody().getHeadAttachmentPoint());

		head.rotateAngleX = pitch / (180F / (float)Math.PI);
		head.rotateAngleY = yaw / (180F / (float)Math.PI);
		head.render(scale);
	}

	@Override
	public void renderLegs(IMutant mutant, float scale, float legSwing, float prevLegSwing) {
		bindTexture();

		float var8 = ((float)Math.PI / 4F);
		this.leg1.rotateAngleZ = -var8;
		this.leg2.rotateAngleZ = var8;
		this.leg3.rotateAngleZ = -var8 * 0.74F;
		this.leg4.rotateAngleZ = var8 * 0.74F;
		this.leg5.rotateAngleZ = -var8 * 0.74F;
		this.leg6.rotateAngleZ = var8 * 0.74F;
		this.leg7.rotateAngleZ = -var8;
		this.leg8.rotateAngleZ = var8;
		float var9 = -0.0F;
		float var10 = 0.3926991F;
		this.leg1.rotateAngleY = var10 * 2.0F + var9;
		this.leg2.rotateAngleY = -var10 * 2.0F - var9;
		this.leg3.rotateAngleY = var10 * 1.0F + var9;
		this.leg4.rotateAngleY = -var10 * 1.0F - var9;
		this.leg5.rotateAngleY = -var10 * 1.0F + var9;
		this.leg6.rotateAngleY = var10 * 1.0F - var9;
		this.leg7.rotateAngleY = -var10 * 2.0F + var9;
		this.leg8.rotateAngleY = var10 * 2.0F - var9;
		float var11 = -(MathHelper.cos(legSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * prevLegSwing;
		float var12 = -(MathHelper.cos(legSwing * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * prevLegSwing;
		float var13 = -(MathHelper.cos(legSwing * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * prevLegSwing;
		float var14 = -(MathHelper.cos(legSwing * 0.6662F * 2.0F + ((float)Math.PI * 3F / 2F)) * 0.4F) * prevLegSwing;
		float var15 = Math.abs(MathHelper.sin(legSwing * 0.6662F + 0.0F) * 0.4F) * prevLegSwing;
		float var16 = Math.abs(MathHelper.sin(legSwing * 0.6662F + (float)Math.PI) * 0.4F) * prevLegSwing;
		float var17 = Math.abs(MathHelper.sin(legSwing * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * prevLegSwing;
		float var18 = Math.abs(MathHelper.sin(legSwing * 0.6662F + ((float)Math.PI * 3F / 2F)) * 0.4F) * prevLegSwing;
		this.leg1.rotateAngleY += var11;
		this.leg2.rotateAngleY += -var11;
		this.leg3.rotateAngleY += var12;
		this.leg4.rotateAngleY += -var12;
		this.leg5.rotateAngleY += var13;
		this.leg6.rotateAngleY += -var13;
		this.leg7.rotateAngleY += var14;
		this.leg8.rotateAngleY += -var14;
		this.leg1.rotateAngleZ += var15;
		this.leg2.rotateAngleZ += -var15;
		this.leg3.rotateAngleZ += var16;
		this.leg4.rotateAngleZ += -var16;
		this.leg5.rotateAngleZ += var17;
		this.leg6.rotateAngleZ += -var17;
		this.leg7.rotateAngleZ += var18;
		this.leg8.rotateAngleZ += -var18;
		Vec3[] legAttachmentPoints = mutant.getBody().getLegAttachmentPoints(mutant.getLegs().getNumberOfLegs());

		MutantUtils.bindToAttachmentPoint(mutant, leg1, legAttachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, leg2, legAttachmentPoints[1]);
		MutantUtils.bindToAttachmentPoint(mutant, leg3, legAttachmentPoints[2]);
		MutantUtils.bindToAttachmentPoint(mutant, leg4, legAttachmentPoints[3]);
		MutantUtils.bindToAttachmentPoint(mutant, leg5, legAttachmentPoints[4]);
		MutantUtils.bindToAttachmentPoint(mutant, leg6, legAttachmentPoints[5]);
		MutantUtils.bindToAttachmentPoint(mutant, leg7, legAttachmentPoints[6]);
		MutantUtils.bindToAttachmentPoint(mutant, leg8, legAttachmentPoints[7]);
		leg1.render(scale);
		leg2.render(scale);
		leg3.render(scale);
		leg4.render(scale);
		leg5.render(scale);
		leg6.render(scale);
		leg7.render(scale);
		leg8.render(scale);
	}

	@Override
	public void renderBody(IMutant mutant, float scale) {
		float h = (float)24 - mutant.getLegHeight() - mutant.getBodyHeight();
		body.setRotationPoint(0, h, 6);
		neck.setRotationPoint(0, h - 1, -9);
		bindTexture();
		body.render(scale);
		neck.render(scale);
	}

	@Override
	public void renderWings(IMutant mutant, float scale, float wingSwing) {}

	@Override
	public void renderArms(IMutant mutant, float scale, float legSwing, float prevLegSwing) {}

	@Override
	public void renderTail(IMutant mutant, float scale, float legSwing, float prevLegSwing) {}

	private void bindTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}

}
