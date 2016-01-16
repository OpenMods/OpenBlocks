package openblocks.client.renderer.tileentity;

import java.util.Collection;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.BlockPos;
import openblocks.common.tileentity.TileEntityGuide;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityGuideRenderer<T extends TileEntityGuide> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(T guide, double x, double y, double z, float partialTicks, int destroyStage) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		float scaleDelta = guide.getTimeSinceChange();
		renderShape(guide.getShape(), guide.getColor(), scaleDelta);
		if (scaleDelta < 1.0) {
			renderShape(guide.getPreviousShape(), guide.getColor(), 1.0f - scaleDelta);
		}
		GL11.glPopMatrix();
	}

	private void renderShape(Collection<BlockPos> shape, int color, float scale) {
		if (shape == null) return;

		RenderUtils.setColor(color);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.disableLighting();

		for (BlockPos coord : shape)
			renderMarkerAt(coord, scale);

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
	}

	private void renderMarkerAt(BlockPos pos, float scale) {
		// TODO 1.8.9 use actual models
	}
}
