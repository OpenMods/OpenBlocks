package openblocks.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGuide;

import org.lwjgl.opengl.GL11;

public class TileEntityGuideRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {
		TileEntityGuide guide = (TileEntityGuide) tileentity;
		bindTextureByName("/terrain.png");

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		OpenRenderHelper.renderCube(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5, OpenBlocks.Blocks.guide, null);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		float scaleDelta = guide.getTimeSinceChange();
		renderShape(guide.getShape(), guide.height, guide.width, guide.depth, x, y, z, scaleDelta);
		if (scaleDelta < 1.0) {
			renderShape(guide.getPreviousShape(), guide.height, guide.width, guide.depth, x, y, z, 1.0f - scaleDelta);
		}
	}

	private void renderShape(boolean[][][] shape, int height, int width,
			int depth, double x, double y, double z, float scale) {
		if (shape == null) {
			return;
		}
		for (int y2 = 0; y2 < shape.length; y2++) {
			for (int x2 = 0; x2 < shape[y2].length; x2++) {
				for (int z2 = 0; z2 < shape[y2][x2].length; z2++) {
					if (shape[y2][x2][z2]) {
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA,
								GL11.GL_ONE_MINUS_SRC_ALPHA);
						renderAt(x + x2 - width, y + y2 - height, z + z2
								- depth, scale);
						GL11.glDisable(GL11.GL_BLEND);
					}
				}
			}
		}
	}

	private void renderAt(double x, double y, double z, float scale) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
		GL11.glPushMatrix();
		GL11.glDisable(2896);
		GL11.glScalef(scale, scale, scale);
		Tessellator t = Tessellator.instance;
		renderBlocks.setRenderBounds(0.05D, 0.05D, 0.05D, 0.95D, 0.95D, 0.95D);
		t.startDrawingQuads();
		t.setColorRGBA(255, 255, 255, 100);
		t.setBrightness(200);
		this.bindTextureByName("/mods/openblocks/textures/blocks/guide.png");
		Icon renderingIcon = OpenBlocks.Blocks.guide.getBlockTextureFromSide(0);
		renderBlocks.renderFaceXNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceXPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceYNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceYPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceZNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		renderBlocks.renderFaceZPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D,
				-0.5D, renderingIcon);
		t.draw();

		GL11.glEnable(2896);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
