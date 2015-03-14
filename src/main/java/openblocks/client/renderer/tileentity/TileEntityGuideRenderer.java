package openblocks.client.renderer.tileentity;

import java.util.Collection;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockGuide.Icons;
import openblocks.common.tileentity.TileEntityGuide;
import openmods.renderer.DisplayListWrapper;
import openmods.utils.Coord;
import openmods.utils.TextureUtils;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TileEntityGuideRenderer extends TileEntitySpecialRenderer {

	private final DisplayListWrapper wrapper = new DisplayListWrapper() {
		@Override
		public void compile() {
			Tessellator t = Tessellator.instance;
			RenderBlocks renderBlocks = new RenderBlocks();
			renderBlocks.setRenderBounds(0.05D, 0.05D, 0.05D, 0.95D, 0.95D, 0.95D);
			t.startDrawingQuads();
			t.setBrightness(200);
			renderBlocks.renderFaceXNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceXPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceYNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceYPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceZNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceZPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			t.draw();
		}
	};

	public TileEntityGuideRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTextuteChange(TextureStitchEvent evt) {
		if (evt.map.getTextureType() == TextureUtils.TEXTURE_MAP_BLOCKS) wrapper.reset();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityGuide guide = (TileEntityGuide)tileentity;

		if (guide.shouldRender()) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			float scaleDelta = guide.getTimeSinceChange();
			renderShape(guide.getShape(), guide.getColor(), scaleDelta);
			if (scaleDelta < 1.0) {
				renderShape(guide.getPreviousShape(), guide.getColor(), 1.0f - scaleDelta);
			}
			GL11.glPopMatrix();
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
			renderAt(coord.x, coord.y, coord.z, scale);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void renderAt(double x, double y, double z, float scale) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y, z + 0.5F);
		GL11.glScalef(scale, scale, scale);
		wrapper.render();
		GL11.glPopMatrix();
	}
}
