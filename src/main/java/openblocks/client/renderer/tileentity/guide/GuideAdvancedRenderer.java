package openblocks.client.renderer.tileentity.guide;

import org.lwjgl.opengl.GL11;

import openblocks.common.tileentity.TileEntityGuide;
import openmods.utils.TextureUtils;

public class GuideAdvancedRenderer implements IGuideRenderer {

	static MarkerRenderer mr;
	
	public GuideAdvancedRenderer(FutureTesselator marker)
	{
		try {
			if (mr == null)
				mr = new MarkerRenderer(marker);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void renderShape(TileEntityGuide guide) {
		
		float scaleDelta = guide.getTimeSinceChange();
		renderShape(guide.getShape(), guide.getColor(), scaleDelta);
		if (scaleDelta < 1.0) {
			renderShape(guide.getPreviousShape(), guide.getColor(), 1.0f - scaleDelta);
		}
		CoordShape toDelete = guide.getAndDeleteShape();
		if (toDelete != null && mr != null)
			mr.deleteShape(toDelete);
	}
	
	private void renderShape(CoordShape shape, int color, float scale) {
		if (shape == null) return;

		TextureUtils.bindDefaultTerrainTexture();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_LIGHTING);

		if (mr != null)
			mr.drawInstanced(shape, color, scale);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	@Override
	public void onTextureChange() {	
	}
}
