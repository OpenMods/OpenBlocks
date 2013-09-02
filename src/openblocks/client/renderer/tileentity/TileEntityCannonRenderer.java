package openblocks.client.renderer.tileentity;

import openblocks.common.tileentity.TileEntityCannon;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCannonRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityCannon cannon = (TileEntityCannon) tileentity;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(0, 0, 0, 0.2f);
		double motionX = cannon.motionX.getValue();
		double motionY = cannon.motionY.getValue();
		double motionZ = cannon.motionZ.getValue();
		float posX = 0.5f;
		float posY = 2.0f;
		float posZ = 0.5f;
			for (int i = 0; i < 100; i++) {
				GL11.glVertex3f(posX, posY, posZ);
				motionY -= 0.03999999910593033D;
				posX += motionX;
				posY += motionY;
				posZ += motionZ;
				motionX *= 0.98;
				motionY *= 0.9800000190734863D;
				motionZ *= 0.98;
			}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
