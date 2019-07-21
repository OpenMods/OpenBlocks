package openblocks.client.renderer.tileentity.guide;

import java.util.function.Supplier;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.shapes.CoordShape;
import openmods.utils.TextureUtils;
import org.lwjgl.opengl.GL11;

public class GuideAdvancedRenderer implements IGuideRenderer {

	private final MarkerRenderer mr = new MarkerRenderer();

	public GuideAdvancedRenderer() {
	}

	@Override
	public void renderShape(TileEntityGuide guide) {
		float scaleDelta = guide.getTimeSinceChange();
		renderShape(guide.getShape(), guide.getColor(), scaleDelta);
		if (scaleDelta < 1.0) renderShape(guide.getPreviousShape(), guide.getColor(), 1.0f - scaleDelta);
		CoordShape toDelete = guide.getAndDeleteShape();
		if (toDelete != null) mr.deleteShape(toDelete);
	}

	private void renderShape(CoordShape shape, int color, float scale) {
		if (shape == null) return;

		GlStateManager.enableBlend();

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.disableLighting();

		TextureUtils.bindTextureToClient(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

		mr.drawInstanced(shape, color, scale);

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
	}

	@Override
	public void onModelBake(Supplier<BufferBuilder> modelSupplier) {
		mr.setModel(modelSupplier);
	}
}
