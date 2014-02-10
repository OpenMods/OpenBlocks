package openblocks.client.gui.pages;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import openmods.gui.component.BaseComponent;
import openmods.tileentity.OpenTileEntity;

import org.lwjgl.opengl.GL11;

public class GuiSpinBlock extends BaseComponent {

	private OpenTileEntity tile;
	private Block block;
	private int meta = 0;
	private static RenderBlocks blockRender = new RenderBlocks();

	public GuiSpinBlock(int x, int y, Block block, OpenTileEntity tile) {
		super(x, y);
		this.block = block;
		if (tile != null) {
			this.tile = tile;
			this.tile.prepareForInventoryRender(block, meta);
		}
	}

	@Override
	public int getWidth() {
		return 64;
	}

	@Override
	public int getHeight() {
		return 64;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 0);
		if (tile != null) {
			TileEntityRenderer.instance.renderTileEntityAt(tile, -0.5, -0.5, -0.5, 0.0F);
		} else {
			drawBlock(minecraft.renderEngine, Tessellator.instance);
		}
		GL11.glPopMatrix();
	}

	private void drawBlock(TextureManager manager, Tessellator t) {
		GL11.glColor4f(1, 1, 1, 1);
		manager.bindTexture(TextureMap.locationBlocksTexture);
		blockRender.setRenderBounds(0, 0, 0, 1, 1, 1);
		t.startDrawingQuads();
		meta = 1;
		blockRender.renderFaceXNeg(Block.stone, -0.5, -0.5, -0.5, block.getIcon(4, meta));
		blockRender.renderFaceXPos(Block.stone, -0.5, -0.5, -0.5, block.getIcon(5, meta));
		blockRender.renderFaceYPos(Block.stone, -0.5, -0.5, -0.5, block.getIcon(1, meta));
		blockRender.renderFaceYNeg(Block.stone, -0.5, -0.5, -0.5, block.getIcon(0, meta));
		blockRender.renderFaceZNeg(Block.stone, -0.5, -0.5, -0.5, block.getIcon(2, meta));
		blockRender.renderFaceZPos(Block.stone, -0.5, -0.5, -0.5, block.getIcon(3, meta));
		t.draw();
	}

}
