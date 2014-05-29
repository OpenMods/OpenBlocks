package openblocks.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.tileentity.TileEntityDigitalFuse;
import openmods.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityDigitalFuseRenderer extends TileEntitySpecialRenderer {
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		TileEntityDigitalFuse fuse = (TileEntityDigitalFuse)tileentity;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 0.128f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		GL11.glRotatef(-BlockUtils.getRotationFromDirection(fuse.getRotation()), 0, 1, 0);

		FontRenderer renderer = func_147498_b();
		if (renderer != null) {
			GL11.glScalef(0.02f, 0.02f, 0.02f);
			GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(-13f, -3f, 0f);
			GL11.glDepthMask(false);
			int timeLeft = fuse.getTimeLeft().getValue();
			String strTimeLeft = String.format("%02d:%02d", (timeLeft % 3600) / 60, (timeLeft % 60));
			renderer.drawString(strTimeLeft, 0, 0, 0);
			GL11.glDepthMask(true);
		}

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
