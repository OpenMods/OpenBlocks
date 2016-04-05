package openblocks.client.renderer.tileentity.guide;

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import openblocks.common.tileentity.TileEntityGuide;
import openmods.renderer.DisplayListWrapper;
import openmods.utils.Coord;
import openmods.utils.TextureUtils;

public class GuideLegacyRenderer implements IGuideRenderer {

	private DisplayListWrapper wrapper;
	
	public GuideLegacyRenderer(final FutureTesselator marker)
	{
		wrapper = new DisplayListWrapper() {
			@Override
			public void compile() {
				marker.render();
				Tessellator.instance.draw();
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
		renderShape(guide.getShape().getCoords(), guide.getColor(), scaleDelta);
		if (scaleDelta < 1.0) {
			renderShape(guide.getPreviousShape().getCoords(), guide.getColor(), 1.0f - scaleDelta);
		}
	}
	
	private void renderShape(Collection<Coord> shape, int color, float scale) {
		if (shape == null) return;

		TextureUtils.bindDefaultTerrainTexture();

		GL11.glColor3ub((byte)(color >> 16), (byte)(color >> 8), (byte)(color >> 0));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_LIGHTING);

		for (Coord coord : shape)
			renderMarkerAt(coord.x, coord.y, coord.z, scale);

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
