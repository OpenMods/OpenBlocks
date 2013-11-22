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

public class MutantRendererZombie implements IMutantRenderer {

	private static final ResourceLocation texture = new ResourceLocation("textures/entity/zombie/zombie.png");

	private ModelRenderer bipedHead;
	private ModelRenderer bipedHeadwear;
	private ModelRenderer bipedBody;
	private ModelRenderer bipedRightArm;
	private ModelRenderer bipedLeftArm;
	private ModelRenderer bipedRightLeg;
	private ModelRenderer bipedLeftLeg;

	@Override
	public void initialize(ModelBase base) {

		base.textureWidth = 64;
		base.textureHeight = 64;

		bipedHead = new ModelRenderer(base, 0, 0);
		bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);

		bipedHeadwear = new ModelRenderer(base, 32, 0);
		bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);

		bipedBody = new ModelRenderer(base, 16, 16);
		bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);

		bipedRightArm = new ModelRenderer(base, 40, 16);
		bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4);

		bipedLeftArm = new ModelRenderer(base, 40, 16);
		bipedLeftArm.mirror = true;
		bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4);

		bipedRightLeg = new ModelRenderer(base, 0, 16);
		bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);

		bipedLeftLeg = new ModelRenderer(base, 0, 16);
		bipedLeftLeg.mirror = true;
		bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
	}

	@Override
	public void renderHead(IMutant mutant, float scale, float yaw, float pitch) {
		bindTexture();
		MutantUtils.bindToAttachmentPoint(mutant, bipedHead, mutant.getBody().getHeadAttachmentPoint());
		bipedHead.rotateAngleX = pitch / (180F / (float)Math.PI);
		bipedHead.rotateAngleY = yaw / (180F / (float)Math.PI);
		bipedHead.render(scale);
	}

	@Override
	public void renderLegs(IMutant mutant, float scale, float legSwing, float prevLegSwing) {

		bindTexture();

		Vec3[] legAttachmentPoints = mutant.getBody().getLegAttachmentPoints(mutant.getLegs().getNumberOfLegs());

		MutantUtils.bindToAttachmentPoint(mutant, bipedLeftLeg, legAttachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, bipedRightLeg, legAttachmentPoints[1]);

		bipedRightLeg.rotateAngleX = MathHelper.cos(legSwing * 0.6662F) * 1.4F * prevLegSwing;
		bipedLeftLeg.rotateAngleX = MathHelper.cos(legSwing * 0.6662F + (float)Math.PI) * 1.4F * prevLegSwing;
		bipedRightLeg.rotateAngleY = 0.0F;
		bipedLeftLeg.rotateAngleY = 0.0F;
		bipedLeftLeg.render(scale);
		bipedRightLeg.render(scale);
	}

	@Override
	public void renderBody(IMutant mutant, float scale) {
		bipedBody.setRotationPoint(0, (float)24 - mutant.getLegHeight() - mutant.getBodyHeight(), 0);
		bindTexture();
		bipedBody.render(scale);
	}

	@Override
	public void renderWings(IMutant mutant, float scale, float wingSwing) {

	}

	@Override
	public void renderArms(IMutant mutant, float scale, float legSwing, float prevLegSwing) {

		bindTexture();

		Vec3[] armAttachmentPoints = mutant.getBody().getArmAttachmentPoints();

		MutantUtils.bindToAttachmentPoint(mutant, bipedLeftArm, armAttachmentPoints[0]);
		MutantUtils.bindToAttachmentPoint(mutant, bipedRightArm, armAttachmentPoints[1]);

		float onGround = mutant.getArmSwingProgress(scale);
		float var8 = MathHelper.sin(onGround * (float)Math.PI);
		float var9 = MathHelper.sin((1.0F - (1.0F - onGround) * (1.0F - onGround)) * (float)Math.PI);
		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;
		this.bipedRightArm.rotateAngleY = -(0.1F - var8 * 0.6F);
		this.bipedLeftArm.rotateAngleY = 0.1F - var8 * 0.6F;
		this.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F);
		this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F);
		this.bipedRightArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
		this.bipedLeftArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
		this.bipedRightArm.rotateAngleZ += MathHelper.cos(legSwing * 0.09F) * 0.05F + 0.05F;
		this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(legSwing * 0.09F) * 0.05F + 0.05F;
		this.bipedRightArm.rotateAngleX += MathHelper.sin(legSwing * 0.067F) * 0.05F;
		this.bipedLeftArm.rotateAngleX -= MathHelper.sin(legSwing * 0.067F) * 0.05F;

		bipedRightArm.render(scale);
		bipedLeftArm.render(scale);
	}

	@Override
	public void renderTail(IMutant mutant, float scale, float legSwing, float prevLegSwing) {}

	private void bindTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}

}
