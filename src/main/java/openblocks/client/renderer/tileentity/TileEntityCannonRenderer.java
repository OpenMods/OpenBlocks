package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import openblocks.client.model.ModelCannon;
import openblocks.common.item.MetasGenericUnstackable;
import openblocks.common.tileentity.TileEntityCannon;
import openmods.OpenMods;

import org.lwjgl.opengl.GL11;

public class TileEntityCannonRenderer extends TileEntitySpecialRenderer {

	private ModelCannon model = new ModelCannon();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/cannon.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityCannon cannon = (TileEntityCannon)tileentity;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glTranslatef(0.5f, 1.0f, 0.5f);

		GL11.glPushMatrix();
		GL11.glRotated(180 - cannon.currentYaw, 0, 1, 0);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		bindTexture(texture);
		model.render(tileentity, f);
		GL11.glPopMatrix();

		if (cannon.renderLine && playerHasCursor()) {
			GL11.glTranslatef(0, -0.5F, 0);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor3f(0, 0, 0);
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
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
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
