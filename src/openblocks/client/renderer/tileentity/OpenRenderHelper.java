package openblocks.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import openblocks.OpenBlocks;

import org.lwjgl.opengl.GL11;

public class OpenRenderHelper {

	protected static RenderBlocks renderBlocks = new RenderBlocks();
	
	public static void renderCube(double x1, double y1, double z1, double x2, double y2, double z2, Block block, Icon overrideTexture) {
		GL11.glPushMatrix();
		GL11.glDisable(2896);
		Tessellator t = Tessellator.instance;
		renderBlocks.setRenderBounds(x1, y1, z1, x2, y2, z2);
		t.startDrawingQuads();

		Icon useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(0);
		renderBlocks.renderFaceYNeg(OpenBlocks.Blocks.guide, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(1);
		renderBlocks.renderFaceYPos(OpenBlocks.Blocks.guide, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(2);
		renderBlocks.renderFaceZNeg(OpenBlocks.Blocks.guide, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(3);
		renderBlocks.renderFaceZPos(OpenBlocks.Blocks.guide, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(4);
		renderBlocks.renderFaceXNeg(OpenBlocks.Blocks.guide, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(5);
		renderBlocks.renderFaceXPos(OpenBlocks.Blocks.guide, 0, 0, 0, useTexture);
		t.draw();

		GL11.glEnable(2896);
		GL11.glPopMatrix();
	}

}
