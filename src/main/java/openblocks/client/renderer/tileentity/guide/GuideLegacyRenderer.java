package openblocks.client.renderer.tileentity.guide;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.BlockPos;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.shapes.CoordShape;
import openmods.renderer.DisplayListWrapper;
import openmods.utils.TextureUtils;
import org.lwjgl.opengl.GL11;

public class GuideLegacyRenderer implements IGuideRenderer {

	private DisplayListWrapper wrapper;

	public GuideLegacyRenderer(final Runnable model) {
		wrapper = new DisplayListWrapper() {
			@Override
			public void compile() {
				// model.run();
				// Tessellator.getInstance().draw();
				// TODO 1.10 models TBD
			}
		};
	}

	@Override
	public void onTextureChange() {
		wrapper.reset();
	}

	@Override
	public void renderShape(TileEntityGuide guide) {
		float scaleDelta = guide.getTimeSinceChange();
		renderShape(guide.getShape(), guide.getColor(), scaleDelta);
		if (scaleDelta < 1.0) {
			renderShape(guide.getPreviousShape(), guide.getColor(), 1.0f - scaleDelta);
		}
	}

	private void renderShape(CoordShape shape, int color, float scale) {
		if (shape == null) return;

		TextureUtils.bindTextureToClient(TextureMap.LOCATION_BLOCKS_TEXTURE);
		// TODO 1.8 GlStateManager
		GL11.glColor3ub((byte)(color >> 16), (byte)(color >> 8), (byte)(color >> 0));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_LIGHTING);

		for (BlockPos coord : shape.getCoords())
			renderMarkerAt(coord.getX(), coord.getY(), coord.getZ(), scale);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void renderMarkerAt(double x, double y, double z, float scale) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y, z + 0.5F);
		GL11.glScalef(scale, scale, scale);
		wrapper.render();
		GL11.glPopMatrix();
	}
}
