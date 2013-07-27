package openblocks.client.renderer.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityValve;
import openblocks.utils.Coord;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;

public class TileEntityValveRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {
		TileEntityValve valve = (TileEntityValve) tileentity;
		renderValve(x, y, z);
		if (valve != null) {
			Set<Coord> coords = valve.getLinkedCoords();
			HashMap<Integer, Double> spread = valve.getSpread();
			if (coords == null) {
				return;
			}
			for (Coord coord : coords) {
				double yLevel = 0;
				if (spread != null && spread.containsKey(coord.y)) {
					yLevel = spread.get(coord.y);
				}
				renderAt(coord.x + x, coord.y + y, coord.z + z, yLevel);
			}
		}
	}


	private void renderValve(double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
		GL11.glPushMatrix();
		GL11.glDisable(2896);

		Tessellator t = Tessellator.instance;
		renderBlocks.setRenderBounds(0.0D, 0, 0.0D, 1.0, 1.0, 1.0);
		t.startDrawingQuads();
		t.setColorRGBA(0, 0, 0, 255);
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


	private void renderAt(double x, double y, double z, double yLevel) {
		if (yLevel == 0) {
			return;
		}
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
		GL11.glPushMatrix();
		GL11.glDisable(2896);

		Tessellator t = Tessellator.instance;
		renderBlocks.setRenderBounds(0.0D, 0, 0.0D, 1.0, yLevel, 1.0);
		t.startDrawingQuads();
		t.setColorRGBA(100, 100, 255, 255);
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
