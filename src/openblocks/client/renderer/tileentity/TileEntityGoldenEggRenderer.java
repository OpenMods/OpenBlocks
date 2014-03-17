package openblocks.client.renderer.tileentity;

import java.util.Random;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLightningBolt;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelEgg;
import openblocks.common.tileentity.TileEntityGoldenEgg;

import org.lwjgl.opengl.GL11;

public class TileEntityGoldenEggRenderer extends TileEntitySpecialRenderer {

	ModelEgg model = new ModelEgg();
	RenderLightningBolt lightningRenderer = new RenderLightningBolt();

	private static final ResourceLocation texture = new ResourceLocation(
			"openblocks", "textures/models/egg.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {
		GL11.glPushMatrix();
		TileEntityGoldenEgg egg = (TileEntityGoldenEgg)tileentity;
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		float rotationSpeed = (18 * egg.animationStageTicks + f) / TileEntityGoldenEgg.ANIMATION_TIME + 1;
		if (egg.worldObj != null && egg.getStage() > 0) {
			egg.rotation += rotationSpeed * f;
			GL11.glRotatef(egg.rotation % 360, 0, 1, 0);
		}
		float amount = (egg.animationStageTicks + f)
				/ TileEntityGoldenEgg.ANIMATION_TIME;
		float totalOffset = amount * 1f; /* 1 being meters */
		if (egg.getStage() == 4) {
			// Animate the egg climbing
			GL11.glTranslatef(0, -totalOffset, 0);
		}
		bindTexture(texture);
		Tessellator.instance.setBrightness(128 + (int)(128 * amount));
		model.render(egg, f, 1f);
		GL11.glPopMatrix();
		if (egg.getStage() >= 4) { /* Render star and phantom block ;) */

			// Not anything like what I wanted
			// renderBeam(egg, Tessellator.instance, f);
			renderPhantom(egg, Tessellator.instance, f);
			renderStar(egg, Tessellator.instance, f);
		}
		GL11.glPopMatrix();
	}

	private static void renderBeam(TileEntityGoldenEgg egg, Tessellator tessellator, float partialTicks) {

		float par2 = 0f, par4 = 0f, par6 = 0f;

		float progress = (egg.animationStageTicks + partialTicks) / TileEntityGoldenEgg.ANIMATION_TIME;

		if (progress > 0.9) {
			GL11.glPushMatrix();
			GL11.glTranslatef(-0.5f, 0f, -0.5f);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glDepthMask(false);
			for (int i = 45; i < 360; i += 90) {
				tessellator.startDrawingQuads();

				double size = 1.2D * ((progress - 0.8) / 0.2f);
				double off = 1 - size;

				tessellator.setColorRGBA(255, 255, 255, (int)(64 * (size / 1.2D)));
				double d18 = off;
				double d19 = off;
				double d20 = size;
				double d21 = off;
				double d22 = off;
				double d23 = size;
				double d24 = size;
				double d25 = size;
				double d26 = -(32.0F * size / 1.2D);
				double d27 = 0.0D;
				double d28 = 1.0D;
				double d29 = -1.0F + 0;
				double d30 = 256.0F;
				tessellator.addVertexWithUV(par2 + d18, par4 + d26, par6 + d19, d28, d30);
				tessellator.addVertexWithUV(par2 + d18, par4 - d26, par6 + d19, d28, d29);
				tessellator.addVertexWithUV(par2 + d20, par4 - d26, par6 + d21, d27, d29);
				tessellator.addVertexWithUV(par2 + d20, par4 + d26, par6 + d21, d27, d30);
				tessellator.addVertexWithUV(par2 + d24, par4 + d26, par6 + d25, d28, d30);
				tessellator.addVertexWithUV(par2 + d24, par4 - d26, par6 + d25, d28, d29);
				tessellator.addVertexWithUV(par2 + d22, par4 - d26, par6 + d23, d27, d29);
				tessellator.addVertexWithUV(par2 + d22, par4 + d26, par6 + d23, d27, d30);
				tessellator.addVertexWithUV(par2 + d20, par4 + d26, par6 + d21, d28, d30);
				tessellator.addVertexWithUV(par2 + d20, par4 - d26, par6 + d21, d28, d29);
				tessellator.addVertexWithUV(par2 + d24, par4 - d26, par6 + d25, d27, d29);
				tessellator.addVertexWithUV(par2 + d24, par4 + d26, par6 + d25, d27, d30);
				tessellator.addVertexWithUV(par2 + d22, par4 + d26, par6 + d23, d28, d30);
				tessellator.addVertexWithUV(par2 + d22, par4 - d26, par6 + d23, d28, d29);
				tessellator.addVertexWithUV(par2 + d18, par4 - d26, par6 + d19, d27, d29);
				tessellator.addVertexWithUV(par2 + d18, par4 + d26, par6 + d19, d27, d30);
				tessellator.draw();
			}
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(true);

			RenderHelper.enableStandardItemLighting();
			GL11.glPopMatrix();
		}
	}

	private void renderPhantom(TileEntityGoldenEgg egg, Tessellator tessellator, float partialTicks) {
		final float SCALE = 1.5f;

		GL11.glPushMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		float progress = (egg.animationStageTicks + partialTicks)
				/ TileEntityGoldenEgg.ANIMATION_TIME;
		GL11.glColor4f(1f, 1f, 1f, 0.2f + 0.4f * progress * 2);
		GL11.glRotatef(egg.rotation % 360, 0, 1, 0);
		GL11.glTranslatef(0, -progress * SCALE * (0.2f + progress * 0.8f), 0);

		tessellator.setBrightness(255);
		model.render(egg, partialTicks, SCALE * (0.2f + progress * 0.8f));

		GL11.glDisable(GL11.GL_BLEND);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
	}

	private static void renderStar(TileEntityGoldenEgg egg, Tessellator tessellator, float partialTicks) {

		GL11.glPushMatrix();
		float f1 = (egg.animationStageTicks + partialTicks)
				/ TileEntityGoldenEgg.ANIMATION_TIME;

		/* Shift down a bit */
		GL11.glTranslatef(0f, 0.5f, 0);
		/* Line up with center of floating egg */
		GL11.glTranslatef(0, -f1, 0);
		/* Rotate opposite direction at 20% speed */
		GL11.glRotatef(egg.rotation * -0.2f % 360, 0.5f, 1, 0.5f);

		/* Configuration tweaks */
		float BEAM_START_DISTANCE = 2F;
		float BEAM_END_DISTANCE = 10f;
		float MAX_OPACITY = 192f;

		RenderHelper.disableStandardItemLighting();
		float f2 = 0.0F;

		if (f1 > 0.8F) {
			f2 = (f1 - 0.8F) / 0.2F;
		}

		Random random = new Random(432L);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glPushMatrix();
		// GL11.glTranslatef(0.0F, -1.0F, -2.0F);

		for (int i = 0; i < (f1 + f1 * f1) / 2.0F * 60.0F; ++i) {
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
			tessellator.startDrawing(6);
			float f3 = random.nextFloat() * BEAM_END_DISTANCE + 5.0F + f2 * 10.0F;
			float f4 = random.nextFloat() * BEAM_START_DISTANCE + 1.0F + f2 * 2.0F;
			tessellator.setBrightness(255);
			tessellator.setColorRGBA_I(16777215, (int)(MAX_OPACITY * (1.0F - f2)));
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.setColorRGBA_I(16766720, 0);
			tessellator.addVertex(-0.866D * f4, f3, -0.5F * f4);
			tessellator.addVertex(0.866D * f4, f3, -0.5F * f4);
			tessellator.addVertex(0.0D, f3, 1.0F * f4);
			tessellator.addVertex(-0.866D * f4, f3, -0.5F * f4);
			tessellator.draw();
		}

		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		RenderHelper.enableStandardItemLighting();

		GL11.glPopMatrix();
	}

}
