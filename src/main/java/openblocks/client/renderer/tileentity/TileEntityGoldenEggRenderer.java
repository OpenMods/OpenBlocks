package openblocks.client.renderer.tileentity;

import java.util.Random;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelEgg;
import openblocks.common.tileentity.TileEntityGoldenEgg;
import openblocks.common.tileentity.TileEntityGoldenEgg.State;

import org.lwjgl.opengl.GL11;

public class TileEntityGoldenEggRenderer extends TileEntitySpecialRenderer {

	private static final float PHANTOM_SCALE = 1.5f;

	private final ModelEgg model = new ModelEgg();

	private static final Random RANDOM = new Random(432L);

	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/egg.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTickTime) {
		GL11.glPushMatrix();
		TileEntityGoldenEgg egg = (TileEntityGoldenEgg)tileentity;
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

		float rotation = egg.getRotation(partialTickTime);
		float progress = egg.getProgress(partialTickTime);
		float offset = egg.getOffset(partialTickTime);

		GL11.glTranslatef(0, -offset, 0);

		GL11.glPushMatrix();
		GL11.glRotatef(rotation, 0, 1, 0);

		bindTexture(texture);
		Tessellator.instance.setBrightness(128 + (int)(128 * progress));
		model.render();

		State state = egg.getState();
		if (state.specialEffects) renderPhantom(rotation, progress, Tessellator.instance, partialTickTime);
		GL11.glPopMatrix();

		if (state.specialEffects) renderStar(rotation, progress, Tessellator.instance, partialTickTime);

		GL11.glPopMatrix();
	}

	private void renderPhantom(float rotation, float progress, Tessellator tessellator, float partialTicks) {
		RenderHelper.disableStandardItemLighting();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		GL11.glColor4f(1f, 1f, 1f, 0.2f + 0.8f * progress);
		float scale = PHANTOM_SCALE * (0.2f + progress * 0.8f);

		tessellator.setBrightness(255);
		GL11.glTranslatef(0, -0.1f * progress, 0);
		GL11.glScalef(scale, scale, scale);
		model.render();

		GL11.glDisable(GL11.GL_BLEND);
		RenderHelper.enableStandardItemLighting();
	}

	private static void renderStar(float rotation, float progress, Tessellator tessellator, float partialTicks) {
		/* Shift down a bit */
		GL11.glTranslatef(0f, 0.5f, 0);
		/* Rotate opposite direction at 20% speed */
		GL11.glRotatef(rotation * -0.2f % 360, 0.5f, 1, 0.5f);

		/* Configuration tweaks */
		float BEAM_START_DISTANCE = 2F;
		float BEAM_END_DISTANCE = 10f;
		float MAX_OPACITY = 192f;

		RenderHelper.disableStandardItemLighting();
		float f2 = 0.0F;

		if (progress > 0.8F) {
			f2 = (progress - 0.8F) / 0.2F;
		}

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);

		RANDOM.setSeed(432L);

		for (int i = 0; i < (progress + progress * progress) / 2.0F * 60.0F; ++i) {
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(RANDOM.nextFloat() * 360.0F + progress * 90.0F, 0.0F, 0.0F, 1.0F);
			tessellator.startDrawing(6);
			float f3 = RANDOM.nextFloat() * BEAM_END_DISTANCE + 5.0F + f2 * 10.0F;
			float f4 = RANDOM.nextFloat() * BEAM_START_DISTANCE + 1.0F + f2 * 2.0F;
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
