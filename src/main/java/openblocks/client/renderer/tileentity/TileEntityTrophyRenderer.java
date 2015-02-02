package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.tileentity.TileEntityTrophy;
import openmods.utils.BlockUtils;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityTrophyRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTick) {
		TileEntityTrophy trophy = (TileEntityTrophy)tileentity;

		Trophy type = trophy.getTrophy();
		if (type != null) {
			ForgeDirection rotation = trophy.getRotation();
			float angle = BlockUtils.getRotationFromDirection(rotation);
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
				Render renderer = RenderManager.instance.getEntityRenderObject(entity);
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
