package openblocks.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelLuggage;
import openblocks.common.entity.EntityLuggage;
import org.lwjgl.opengl.GL11;

public class EntityLuggageRenderer extends MobRenderer<EntityLuggage> {

	private static final ModelBase luggageModel = new ModelLuggage();

	private static final ResourceLocation textureNormal = OpenBlocks.location("textures/models/luggage.png");
	private static final ResourceLocation textureSpecial = OpenBlocks.location("textures/models/luggage_special.png");
	private static final ResourceLocation creeperEffect = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

	private class LayerCharge implements LayerRenderer<EntityLuggage> {

		@Override
		public void doRenderLayer(EntityLuggage luggage, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
			if (luggage.isSpecial()) {
				bindTexture(creeperEffect);
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.loadIdentity();
				float f = luggage.ticksExisted + partialTicks;
				GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.enableBlend();
				GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(1, 1);
				luggageModel.render(luggage, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
			}
		}

		@Override
		public boolean shouldCombineTextures() {
			return false;
		}
	}

	public EntityLuggageRenderer(EntityRendererManager renderManager) {
		super(renderManager, luggageModel, 0.5F);
		addLayer(new LayerCharge());
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLuggage entity) {
		return entity.isSpecial()? textureSpecial : textureNormal;
	}

}
