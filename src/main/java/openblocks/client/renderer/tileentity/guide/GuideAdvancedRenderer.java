package openblocks.client.renderer.tileentity.guide;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.shapes.CoordShape;
import openmods.utils.TextureUtils;
import org.lwjgl.opengl.GL11;

public class GuideAdvancedRenderer implements IGuideRenderer {

	private final MarkerRenderer mr;

	public GuideAdvancedRenderer(Runnable marker) {
		this.mr = new MarkerRenderer(marker);
	}

	@Override
	public void renderShape(TileEntityGuide guide) {
		float scaleDelta = guide.getTimeSinceChange();
		renderShape(guide.getShape(), guide.getColor(), scaleDelta);
		if (scaleDelta < 1.0) renderShape(guide.getPreviousShape(), guide.getColor(), 1.0f - scaleDelta);
		CoordShape toDelete = guide.getAndDeleteShape();
		if (toDelete != null && mr != null) mr.deleteShape(toDelete);
	}

	private void renderShape(CoordShape shape, int color, float scale) {
		if (shape == null) return;

		TextureUtils.bindTextureToClient(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.enableBlend();

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.disableLighting();

		mr.drawInstanced(shape, color, scale);

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
	}

	@Override
	public void onTextureChange() {
		mr.reset();
	}
}
