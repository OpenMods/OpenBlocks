package openblocks.client.renderer.tileentity;

import java.util.Collection;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.BlockPos;
import openblocks.common.tileentity.TileEntityGuide;

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

		GL11.glColor3ub((byte)(color >> 16), (byte)(color >> 8), (byte)(color >> 0));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_LIGHTING);

		for (BlockPos coord : shape)
			renderMarkerAt(coord, scale);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void renderMarkerAt(BlockPos pos, float scale) {
		// TODO 1.8.9 use actual models
	}
}
