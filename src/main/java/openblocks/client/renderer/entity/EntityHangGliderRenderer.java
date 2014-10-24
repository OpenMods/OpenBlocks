package openblocks.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import openblocks.common.entity.EntityHangGlider;

import org.lwjgl.opengl.GL11;

public class EntityHangGliderRenderer extends Render {

	private static final float QUAD_HALF_SIZE = 2.4f;
	private static final float ONGROUND_ROTATION = 90f;

	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/hangglider.png");

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1) {
		final EntityHangGlider glider = (EntityHangGlider)entity;
		final EntityPlayer owner = glider.getPlayer();
		if (owner == null) return;

		final Minecraft minecraft = Minecraft.getMinecraft();
		final boolean isLocalPlayer = owner == minecraft.thePlayer;
		final boolean isFpp = minecraft.gameSettings.thirdPersonView == 0;
		final boolean playerOnGround = glider.isPlayerOnGround();

		if (isLocalPlayer && isFpp && playerOnGround) return;

		final float rotation = interpolateRotation(glider.prevRotationYaw, glider.rotationYaw, f1);

		GL11.glPushMatrix();

		GL11.glTranslated(x, y, z);
		GL11.glRotatef(180.0F - rotation, 0.0F, 1.0F, 0.0F);

		if (isLocalPlayer) {
			if (playerOnGround) {
				// move up and closer to back
				GL11.glTranslated(0, -0.2, +0.3);
			} else {
				if (isFpp) {
					// move over head when flying in FPP
					GL11.glTranslated(0, +0.2, 0);
				} else {
					// move closer to back and forward when flying in TDD
					GL11.glTranslated(0, -0.8, -1.0);
				}
			}
		} else {
			if (playerOnGround) {
				// move up little bit (other player center is lower)
				GL11.glTranslated(0, +0.2, +0.3);
			} else {
				// move closer to back and forward when flying
				GL11.glTranslated(0, -0.5, -1.0);
			}
		}

		if (playerOnGround) {
			GL11.glRotatef(ONGROUND_ROTATION, 1, 0, 0);
			GL11.glScalef(0.4f, 1f, 0.4f);
		}

		bindTexture(texture);
		renderGlider();

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
		return texture;
	}
}
