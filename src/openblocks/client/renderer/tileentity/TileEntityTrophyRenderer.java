package openblocks.client.renderer.tileentity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.common.TrophyHandler;
import openblocks.common.TrophyHandler.Trophy;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityTrophyRenderer extends TileEntitySpecialRenderer {
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f) {
		TileEntityTrophy trophy = (TileEntityTrophy) tileentity;
		Trophy type = trophy.getTrophyType();
		if (type != null) {
			Entity entity = type.getEntity();
			if (entity != null) {
				double width = entity.width * 4;
				double height = entity.height * 1.5;
				double ratio = type.getScale();
				entity.worldObj = tileentity.worldObj;
				GL11.glPushMatrix();
				GL11.glTranslated(d0, d1, d2);
				GL11.glTranslated(0.5, 0.2 + type.getVerticalOffset(), 0.5);
				GL11.glScaled(ratio, ratio, ratio);
				Render renderer = RenderManager.instance.getEntityRenderObject(entity);
				// yeah we dont care about fonts, but we do care that the renderManager is available
				if (renderer.getFontRendererFromRenderManager() != null) {
					renderer.doRender(entity, 0, 0, 0, f, 0.5f);
				}
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glTranslated(d0, d1, d2);
		        GL11.glColor4f(1, 1, 1, 1);
				bindTextureByName("/mods/openblocks/textures/blocks/tank.png");
				OpenRenderHelper.renderCube(0.2, 0, 0.2, 0.8, 0.2, 0.8, OpenBlocks.Blocks.trophy, null);
				GL11.glPopMatrix();
			}
		}
	}

}
