package openblocks.client.renderer.mutant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import openblocks.api.IMutant;
import openblocks.api.IMutantRenderer;
import openblocks.client.model.ModelMutant;
import openmods.utils.MutantUtils;

public class MutantRendererOcelot implements IMutantRenderer {

	private static final ResourceLocation blackOcelotTextures = new ResourceLocation("textures/entity/cat/black.png");
    private static final ResourceLocation ocelotTextures = new ResourceLocation("textures/entity/cat/ocelot.png");
    private static final ResourceLocation redOcelotTextures = new ResourceLocation("textures/entity/cat/red.png");
    private static final ResourceLocation siameseOcelotTextures = new ResourceLocation("textures/entity/cat/siamese.png");
	
	private ModelRenderer tail1;
	private ModelRenderer tail2;
	private ModelRenderer head;
	private ModelRenderer body;

	private ModelRenderer leg1;
	private ModelRenderer leg2;
	private ModelRenderer leg3;
	private ModelRenderer leg4;

	@Override
	public void initialize(ModelBase base) {
		ModelMutant mutantModel = (ModelMutant)base;
		mutantModel._setTextureOffset("head.main", 0, 0);
		mutantModel._setTextureOffset("head.nose", 0, 24);
		mutantModel._setTextureOffset("head.ear1", 0, 10);
		mutantModel._setTextureOffset("head.ear2", 6, 10);
		head = new ModelRenderer(base, "head");
		head.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5);
		head.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2);
		head.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2);
		head.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2);

		tail1 = new ModelRenderer(base, 0, 15);
		tail1.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
		tail1.rotateAngleX = 0.9F;
		tail2 = new ModelRenderer(base, 4, 15);
		tail2.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);

		body = new ModelRenderer(base, 20, 0);
		body.addBox(-2, -8, -6, 4, 16, 6);

		leg1 = new ModelRenderer(base, 8, 13);
		leg1.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2);

		leg2 = new ModelRenderer(base, 8, 13);
		leg2.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2);

		leg3 = new ModelRenderer(base, 8, 13);
		leg3.addBox(-1.0F, 0.0F, -0.0F, 2, 6, 2);

		leg4 = new ModelRenderer(base, 8, 13);
		leg4.addBox(-1.0F, 0.0F, -0.0F, 2, 6, 2);
	}

	@Override
	public void renderHead(IMutant mutant, float scale, float yaw, float pitch) {
		bindTexture(ocelotTextures);
		MutantUtils.bindToAttachmentPoint(mutant, head, mutant.getBody().getHeadAttachmentPoint());
		head.rotateAngleX = pitch / (180F / (float) Math.PI);
        head.rotateAngleY = yaw / (180F / (float) Math.PI);
        head.render(scale);
	}

	@Override
	public void renderLegs(IMutant mutant, float scale, float legSwing, float prevLegSwing) {
		bindTexture(ocelotTextures);
		Vec3[] attachmentPoints = mutant.getBody().getLegAttachmentPoints(mutant.getNumberOfLegs());
		
		MutantUtils.bindToAttachmentPoint(mutant, leg1, attachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, leg2, attachmentPoints[1]);
		MutantUtils.bindToAttachmentPoint(mutant, leg3, attachmentPoints[2]);
		MutantUtils.bindToAttachmentPoint(mutant, leg4, attachmentPoints[3]);
		
		leg1.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 1.4F * prevLegSwing;
        leg2.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float) Math.PI) * 1.4F * prevLegSwing;
        leg3.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float) Math.PI) * 1.4F * prevLegSwing;
        leg4.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 1.4F * prevLegSwing;

        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        leg4.render(scale);
	}

	@Override
	public void renderBody(IMutant mutant, float scale) {
		bindTexture(ocelotTextures);
		body.setRotationPoint(0, (float) 24 - mutant.getLegHeight() - mutant.getBodyHeight(), 1.0F);
        body.rotateAngleX = ((float) Math.PI / 2F);
        body.render(scale);
	}

	@Override
	public void renderWings(IMutant mutant, float scale, float wingSwing) { }

	@Override
	public void renderArms(IMutant mutant, float scale, float legSwing, float prevLegSwing) { }

	@Override
	public void renderTail(IMutant mutant, float scale, float legSwing, float prevLegSwing) {
		bindTexture(ocelotTextures);
		Vec3 attachmentPoint = mutant.getBody().getTailAttachmentPoint();
		tail2.setRotationPoint(
                 (float) attachmentPoint.xCoord,
                 (float) (24 - mutant.getLegHeight() - mutant.getBodyHeight() - attachmentPoint.yCoord) + 5,
                 (float) attachmentPoint.zCoord + 6
				);
		MutantUtils.bindToAttachmentPoint(mutant, tail1, attachmentPoint);

		tail2.rotateAngleX = 1.7278761F + ((float)Math.PI / 4F) * MathHelper.cos(legSwing) * prevLegSwing;
		tail1.render(scale);
		tail2.render(scale);
	}
	
	private void bindTexture(ResourceLocation res) {
		Minecraft.getMinecraft().renderEngine.bindTexture(res);
	}
}
