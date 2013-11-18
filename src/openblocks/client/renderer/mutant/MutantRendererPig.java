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

public class MutantRendererPig implements IMutantRenderer {

	private static final ResourceLocation texture = new ResourceLocation("textures/entity/pig/pig.png");

	private ModelRenderer leg1;
	private ModelRenderer leg2;
	private ModelRenderer leg3;
	private ModelRenderer leg4;
	private ModelRenderer body;
	private ModelRenderer head;

	@Override
	public void initialize(ModelBase base) {
		leg1 = new ModelRenderer(base, 0, 16);
		leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4);
		leg2 = new ModelRenderer(base, 0, 16);
		leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4);
		leg3 = new ModelRenderer(base, 0, 16);
		leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4);
		leg4 = new ModelRenderer(base, 0, 16);
		leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4);

		body = new ModelRenderer(base, 28, 8);
		body.addBox(-5.0F, -9.0F, -8.0F, 10, 16, 8);

		head = new ModelRenderer(base, 0, 0);
		head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8);

		head.setTextureOffset(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1);
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

		Vec3[] legAttachmentPoints = mutant.getBody().getLegAttachmentPoints(mutant.getLegs().getNumberOfLegs());

		MutantUtils.bindToAttachmentPoint(mutant, leg1, legAttachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, leg2, legAttachmentPoints[1]);
		MutantUtils.bindToAttachmentPoint(mutant, leg3, legAttachmentPoints[2]);
		MutantUtils.bindToAttachmentPoint(mutant, leg4, legAttachmentPoints[3]);

		leg1.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 1.4F * prevLegSwing;
		leg2.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float)Math.PI) * 1.4F * prevLegSwing;
		leg3.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float)Math.PI) * 1.4F * prevLegSwing;
		leg4.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 1.4F * prevLegSwing;

		leg1.render(scale);
		leg2.render(scale);
		leg3.render(scale);
		leg4.render(scale);
	}

	@Override
	public void renderBody(IMutant mutant, float scale) {
		bindTexture();
		body.setRotationPoint(0, (float)24 - mutant.getLegHeight() - mutant.getBodyHeight(), 1.0F);
		body.rotateAngleX = ((float)Math.PI / 2F);
		body.render(scale);
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
