package openblocks.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

public class OpenRenderHelper {

	public static RenderBlocks renderBlocks = new RenderBlocks();

	public static void renderCube(double x1, double y1, double z1, double x2, double y2, double z2, Block block, Icon overrideTexture) {
		renderCube(x1, y1, z1, x2, y2, z2, block, overrideTexture, 0);
	}
	
	public static void renderCube(double x1, double y1, double z1, double x2, double y2, double z2, Block block, Icon overrideTexture, int meta) {
		GL11.glPushMatrix();
		Tessellator t = Tessellator.instance;

		GL11.glColor4f(1, 1, 1, 1);
		
		renderBlocks.setRenderBounds(x1, y1, z1, x2, y2, z2);

		t.startDrawingQuads();

		Icon useTexture = overrideTexture != null? overrideTexture : block.getIcon(0, meta);
		t.setNormal(0.0F, -1.0F, 0.0F);
		renderBlocks.renderFaceYNeg(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getIcon(1, meta);
		t.setNormal(0.0F, 1.0F, 0.0F);
		renderBlocks.renderFaceYPos(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getIcon(2, meta);
		t.setNormal(0.0F, 0.0F, -1.0F);
		renderBlocks.renderFaceZNeg(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getIcon(3, meta);
		t.setNormal(0.0F, 0.0F, 1.0F);
		renderBlocks.renderFaceZPos(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getIcon(4, meta);
		t.setNormal(-1.0F, 0.0F, 0.0F);
		renderBlocks.renderFaceXNeg(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getIcon(5, meta);
		t.setNormal(1.0F, 0.0F, 0.0F);
		renderBlocks.renderFaceXPos(block, 0, 0, 0, useTexture);
		t.draw();

		GL11.glPopMatrix();
	}

	public static void renderWorldCube(RenderBlocks renderer, double x1, double y1, double z1, double x2, double y2, double z2, Block block, Icon overrideTexture) {

		renderer.setRenderBounds(x1, y1, z1, x2, y2, z2);

		Icon useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(0);
		renderer.renderFaceYNeg(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(1);
		renderer.renderFaceYPos(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(2);
		renderer.renderFaceZNeg(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(3);
		renderer.renderFaceZPos(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(4);
		renderer.renderFaceXNeg(block, 0, 0, 0, useTexture);

		useTexture = overrideTexture != null? overrideTexture : block.getBlockTextureFromSide(5);
		renderer.renderFaceXPos(block, 0, 0, 0, useTexture);
	}

}
