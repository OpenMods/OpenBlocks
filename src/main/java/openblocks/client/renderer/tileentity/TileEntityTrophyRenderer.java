package openblocks.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.tileentity.TileEntityTrophy;
import openmods.utils.BlockUtils;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityTrophyRenderer extends TileEntitySpecialRenderer<TileEntityTrophy> {

	@Override
	public void renderTileEntityAt(TileEntityTrophy trophy, double x, double y, double z, float partialTick, int destroyProgress) {

		Trophy type = trophy.getTrophy();
		if (type != null) {
			float angle = BlockUtils.getRotationFromOrientation(trophy.getOrientation());
			renderTrophy(type, x + 0.5, y, z + 0.5, angle);
		}
	}

	public static void renderTrophy(Trophy type, double x, double y, double z, float angle) {
		Entity entity = type.getEntity();
		if (entity != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y + type.getVerticalOffset() + 0.2, z);
			GL11.glRotatef(angle, 0, 1, 0);

			final double ratio = type.getScale();
			GL11.glScaled(ratio, ratio, ratio);
			World renderWorld = RenderUtils.getRenderWorld();
			if (renderWorld != null) {
				Render<Entity> renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
				// yeah we don't care about fonts, but we do care that the
				// renderManager is available
				if (renderer != null && renderer.getFontRendererFromRenderManager() != null) {
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					RenderUtils.enableLightmap();

					synchronized (entity) {
						entity.worldObj = renderWorld;
						renderer.doRender(entity, 0, 0, 0, 0, 0);
						entity.worldObj = null;
					}

					GL11.glPopAttrib();
				}
			}
			GL11.glPopMatrix();

		}
	}

}
