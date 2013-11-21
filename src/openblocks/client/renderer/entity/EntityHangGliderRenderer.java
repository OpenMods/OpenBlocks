package openblocks.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import openblocks.common.entity.EntityHangGlider;
import openmods.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

public class EntityHangGliderRenderer extends Render {

	private static final float QUAD_HALF_SIZE = 2.4f;
	private static final float ONGROUND_ROTATION = 90f;

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1) {

		EntityHangGlider glider = (EntityHangGlider)entity;

		GL11.glPushMatrix();

		float rotation = interpolateRotation(glider.prevRotationYaw, glider.rotationYaw, f1);
		double x2 = Math.cos(Math.toRadians(rotation + 90)) * 1.5;
		double z2 = Math.sin(Math.toRadians(rotation + 90)) * 1.5;

		/* Only shift to first person if FP and we're on glider */
		if (glider.getPlayer() == Minecraft.getMinecraft().thePlayer
				&& Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			GL11.glTranslatef((float)x, (float)y + 0.4f, (float)z);
		} else {
			if (glider.getPlayer() != null && glider.isPlayerOnGround()) {
				GL11.glTranslatef((float)x, (float)y - 0.2f, (float)z);
			} else {
				GL11.glTranslatef((float)x + (float)x2, (float)y - 0.2f, (float)z
						+ (float)z2);
			}
		}

		// Maybe this should be pushed in to the matrix and popped out too
		GL11.glRotatef(180.0F - rotation, 0.0F, 1.0F, 0.0F);

		/* Rotate glider backwards when the player hits the ground */
		if (glider.getPlayer() != null && glider.isPlayerOnGround()) {
			GL11.glTranslatef(0f, 0f, 0.3f);
			GL11.glRotatef(ONGROUND_ROTATION, 1f, 0f, 0f);
			GL11.glScalef(0.4f, 1f, 0.4f);
		}

		// Push matrix to hold it's location for rendering other stuff */
		GL11.glPushMatrix();
		CompatibilityUtils.bindTextureToClient("textures/models/hangglider.png");
		renderGlider();
		GL11.glPopMatrix();

		// Render other stuff here if you wish
		GL11.glPopMatrix();
	}

	private static void renderGlider() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.setColorRGBA(255, 255, 255, 255);
		t.addVertexWithUV(QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE, 1, 1);
		t.addVertexWithUV(-QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE, 0, 1);
		t.addVertexWithUV(-QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE, 0, 0);
		t.addVertexWithUV(QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE, 1, 0);
		t.draw();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	/* Interpolate rotation */
	private static float interpolateRotation(float prevRotation, float nextRotation, float modifier) {
		float rotation = nextRotation - prevRotation;

		while (rotation < -180.0F)
			rotation += 360.0F;

		while (rotation >= 180.0F) {
			rotation -= 360.0F;
		}

		return prevRotation + modifier * rotation;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return CompatibilityUtils.getResourceLocation("textures/models/hangglider.png");
	}
}
