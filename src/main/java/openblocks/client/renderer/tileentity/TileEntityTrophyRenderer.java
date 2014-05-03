package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.tileentity.TileEntityTrophy;
import openmods.utils.BlockUtils;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityTrophyRenderer extends TileEntitySpecialRenderer {

	public static boolean renderWithLighting = true;

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f) {
		TileEntityTrophy trophy = (TileEntityTrophy)tileentity;

		Trophy type = trophy.getTrophy();
		if (type != null) {
			Entity entity = type.getEntity();
			if (entity != null) {
				double ratio = type.getScale();
				GL11.glPushMatrix();
				GL11.glTranslated(d0, d1, d2);
				GL11.glTranslated(0.5, 0.2 + type.getVerticalOffset(), 0.5);
				GL11.glRotatef(BlockUtils.getRotationFromDirection(trophy.getRotation()), 0, 1, 0);

				GL11.glScaled(ratio, ratio, ratio);
				entity.worldObj = RenderUtils.getRenderWorld();
				if (entity.worldObj != null) {
					Render renderer = RenderManager.instance.getEntityRenderObject(entity);
					// yeah we don't care about fonts, but we do care that the
					// renderManager is available
					if (renderer != null && renderer.getFontRendererFromRenderManager() != null) {
						GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
						if (renderWithLighting) RenderUtils.enableLightmap();
						renderer.doRender(entity, 0, 0, 0, 0, 0);
						GL11.glPopAttrib();
					}
				}
				entity.worldObj = null;
				GL11.glPopMatrix();

			}
		}
	}

}
