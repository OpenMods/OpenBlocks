package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelCannon;
import openblocks.common.item.MetasGenericUnstackable;
import openblocks.common.tileentity.TileEntityCannon;
import openmods.OpenMods;

import org.lwjgl.opengl.GL11;

public class TileEntityCannonRenderer extends TileEntitySpecialRenderer<TileEntityCannon> {

	private ModelCannon model = new ModelCannon();
	private static final ResourceLocation texture = OpenBlocks.location("textures/models/cannon.png");

	@Override
	public void renderTileEntityAt(TileEntityCannon cannon, double x, double y, double z, float partialTick, int destroyProgress) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glTranslatef(0.5f, 1.0f, 0.5f);

		GL11.glPushMatrix();
		GL11.glRotated(180 - cannon.currentYaw, 0, 1, 0);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		bindTexture(texture);
		model.render(cannon, partialTick);
		GL11.glPopMatrix();

		if (cannon.renderLine && playerHasCursor()) {
			GL11.glTranslatef(0, -0.5F, 0);
			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			GlStateManager.color(0, 0, 0);
			GL11.glBegin(GL11.GL_LINE_STRIP);

			final Vec3 motion = cannon.getMotion();
			double motionX = motion.xCoord;
			double motionY = motion.yCoord;
			double motionZ = motion.zCoord;
			float posX = 0f;
			float posY = 0f;
			float posZ = 0f;
			for (int i = 0; i < 200; i++) {
				GL11.glVertex3f(posX, posY, posZ);
				motionY -= 0.03999999910593033D;
				posX += motionX;
				posY += motionY;
				posZ += motionZ;
				// motionX *= 0.98;
				// motionY *= 0.9800000190734863D;
				// motionZ *= 0.98;
			}
			GL11.glEnd();
			GlStateManager.enableCull();
			GlStateManager.enableTexture2D();
		} else cannon.renderLine = true;
		GL11.glPopMatrix();
	}

	private static boolean playerHasCursor() {
		EntityPlayer player = OpenMods.proxy.getThePlayer();
		if (player == null) return false;
		ItemStack held = player.getHeldItem();
		return held != null && MetasGenericUnstackable.pointer.isA(held);
	}

}
