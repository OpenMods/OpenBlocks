package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import openblocks.Config;
import openblocks.common.tileentity.*;
import openblocks.common.tileentity.TileEntityImaginary.ICollisionData;
import openblocks.common.tileentity.TileEntityImaginary.PanelData;
import openblocks.common.tileentity.TileEntityImaginary.Property;
import openblocks.common.tileentity.TileEntityImaginary.StairsData;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityImaginaryRenderer extends TileEntitySpecialRenderer<TileEntityImaginary> {

	@Override
	public void renderTileEntityAt(TileEntityImaginary te, double x, double y, double z, float partialTicks, int destroyProgress) {
		boolean isVisible = te.is(Property.VISIBLE);

		if (isVisible && te.visibility < 1) te.visibility = Math.min(te.visibility + Config.imaginaryFadingSpeed, 1);
		else if (!isVisible && te.visibility > 0) te.visibility = Math.max(te.visibility - Config.imaginaryFadingSpeed, 0);

		if (te.visibility <= 0) return;

		bindTexture(TextureMap.locationBlocksTexture);

		if (!te.isPencil()) {
			RenderUtils.setColor(te.color, te.visibility);
		} else {
			GlStateManager.color(1, 1, 1, te.visibility);
		}

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		final ICollisionData data = te.collisionData;

		if (data instanceof PanelData) {
			PanelData pd = (PanelData)data;

			GL11.glTranslated(0, pd.height, 0);
			renderPanel(te.isPencil());
		} else if (data instanceof StairsData) {
			StairsData sd = (StairsData)data;

			GL11.glTranslated(0.5, 0, 0.5);

			switch (sd.orientation) {
				case NORTH:
					break;
				case EAST:
					GL11.glRotatef(-90, 0, 1, 0);
					break;
				case SOUTH:
					GL11.glRotatef(180, 0, 1, 0);
					break;
				case WEST:
					GL11.glRotatef(90, 0, 1, 0);
					break;
				default:
					break;
			}

			renderStairs(te.isPencil());
		} else {
			renderBlock(te.isPencil());
		}

		GL11.glPopMatrix();
	}

	private void renderBlock(boolean isPencil) {
		// TODO 1.8.9 implement
	}

	private void renderStairs(boolean isPencil) {
		// TODO 1.8.9 implement
	}

	private void renderPanel(boolean isPencil) {
		// TODO 1.8.9 implement
	}
}
