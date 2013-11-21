package openblocks.client.renderer.mutant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import openblocks.api.IMutant;
import openblocks.api.IMutantRenderer;
import openmods.utils.MutantUtils;

public class MutantRendererCreeper implements IMutantRenderer {

	private static final ResourceLocation texture = new ResourceLocation("textures/entity/creeper/creeper.png");
	
	private ModelRenderer body;
	private ModelRenderer leg1;
	private ModelRenderer leg2;
	private ModelRenderer leg3;
	private ModelRenderer leg4;
	private ModelRenderer head;

	public MutantRendererCreeper() {
	}
	
	public void initialize(ModelBase base) {

		base.textureWidth = 64;
		base.textureHeight = 32;
		
		head = new ModelRenderer(base, 0, 0);
		head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);

		body = new ModelRenderer(base, 16, 16);
		body.addBox(-4.0F, 0, -2.0F, 8, 12, 4);

		leg1 = new ModelRenderer(base, 0, 16);
		leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4);

		leg2 = new ModelRenderer(base, 0, 16);
		leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4);

		leg3 = new ModelRenderer(base, 0, 16);
		leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4);

		leg4 = new ModelRenderer(base, 0, 16);
		leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4);
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
		body.setRotationPoint(0, (float) 24 - mutant.getLegHeight() - mutant.getBodyHeight(), 0);
        body.render(scale);
	}

	@Override
	public void renderWings(IMutant mutant, float scale, float wingSwing) { }

	@Override
	public void renderArms(IMutant mutant, float scale, float legSwing, float prevLegSwing) { }

	@Override
	public void renderTail(IMutant mutant, float scale, float legSwing, float prevLegSwing) { }
	
	private void bindTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}

}
