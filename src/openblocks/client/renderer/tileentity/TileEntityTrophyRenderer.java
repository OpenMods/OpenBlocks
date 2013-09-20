package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import openblocks.OpenBlocks;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityTrophyRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f) {
		TileEntityTrophy trophy = (TileEntityTrophy)tileentity;
		GL11.glPushMatrix();

		Trophy type = trophy.getTrophyType();
		if (type != null) {
			Entity entity = type.getEntity();
			if (entity != null) {
				double ratio = type.getScale();
				entity.worldObj = tileentity.worldObj;
				GL11.glPushMatrix();
				GL11.glTranslated(d0, d1, d2);
				GL11.glTranslated(0.5, 0.2 + type.getVerticalOffset(), 0.5);
				GL11.glRotatef(BlockUtils.getRotationFromDirection(trophy.getRotation()), 0, 1, 0);

				GL11.glScaled(ratio, ratio, ratio);
				Render renderer = RenderManager.instance.getEntityRenderObject(entity);
				// yeah we dont care about fonts, but we do care that the
				// renderManager is available
				if (renderer.getFontRendererFromRenderManager() != null) {
					renderer.doRender(entity, 0, 0, 0, f, 0.5f);
				}
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glTranslated(d0, d1, d2);

				//TODO: fix
				bindTexture(TextureMap.locationBlocksTexture);
				OpenRenderHelper.renderCube(0.2, 0, 0.2, 0.8, 0.2, 0.8, OpenBlocks.Blocks.trophy, null);
				GL11.glPopMatrix();

			}
		}
		GL11.glPopMatrix();
	}

}
