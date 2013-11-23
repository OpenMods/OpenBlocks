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
import openmods.OpenMods;

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
		TileEntityGoldenEgg egg = (TileEntityGoldenEgg) tileentity;
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.0f, (float) z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		float rotationSpeed = (10 * egg.animationStageTicks + f) / TileEntityGoldenEgg.STAGE_CHANGE_TICK + 1;
		if (egg.worldObj != null && egg.getStage() > 0) {
			egg.rotation += rotationSpeed * f;
			GL11.glRotatef(egg.rotation % 360,
					0, 1, 0);
		}
		if (egg.getStage() == 4) {
			// Animate the egg climbing
			float amount = (egg.animationStageTicks + f)
					/ TileEntityGoldenEgg.STAGE_CHANGE_TICK;
			float totalOffset = amount * 1f; /* 1 being meters */
			GL11.glTranslatef(0, -totalOffset, 0);
		}
		bindTexture(texture);
		model.render(egg, f);
		if (egg.getStage() == 4) {
			renderStar(egg, Tessellator.instance, f);
		}
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	private void renderStar(TileEntityGoldenEgg egg, Tessellator tessellator,float par2) {
		/* Configuration tweaks */
		float BEAM_START_DISTANCE = 2F;
		float BEAM_END_DISTANCE = 10f;
		
		RenderHelper.disableStandardItemLighting();
		float f1 = ((float) egg.animationStageTicks + par2)
				/ TileEntityGoldenEgg.STAGE_CHANGE_TICK;
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

		for (int i = 0; (float) i < (f1 + f1 * f1) / 2.0F * 60.0F; ++i) {
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F,0.0F, 1.0F);
			tessellator.startDrawing(6);
			float f3 = random.nextFloat() * BEAM_END_DISTANCE + 5.0F + f2 * 10.0F;
			float f4 = random.nextFloat() * BEAM_START_DISTANCE + 1.0F + f2 * 2.0F;
			tessellator.setColorRGBA_I(16777215, (int) (255.0F * (1.0F - f2)));
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.setColorRGBA_I(16711935, 0);
			tessellator.addVertex(-0.866D * (double) f4, (double) f3,(double) (-0.5F * f4));
			tessellator.addVertex(0.866D * (double) f4, (double) f3,(double) (-0.5F * f4));
			tessellator.addVertex(0.0D, (double) f3, (double) (1.0F * f4));
			tessellator.addVertex(-0.866D * (double) f4, (double) f3,(double) (-0.5F * f4));
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
	}

}
