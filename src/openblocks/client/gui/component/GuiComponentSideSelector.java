package openblocks.client.gui.component;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;

public class GuiComponentSideSelector extends BaseComponent {

	RenderBlocks blockRender = new RenderBlocks();

	public double scale;
	private int rotX = -10;
	private int rotY = 10;
	
	private int startClickX = 0;
	private int startClickY = 0;
	
	public GuiComponentSideSelector(int x, int y, double scale) {
		super(x, y);
		this.scale = scale;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		Tessellator t = Tessellator.instance;
		GL11.glTranslated(offsetX + x + (scale / 2), offsetY + y + (scale / 2), scale);
		GL11.glScaled(scale, scale, scale);
		//GL11.glRotated(rot, 1, 0, 0);
		GL11.glRotated(rotX, 1, 0, 0);
		GL11.glRotated(rotY, 0, 1, 0);
		//GL11.glRotated(rot++, 0, 0, 1);
		GL11.glColor4f(1, 1, 1, 1);
		minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		blockRender.setRenderBounds(0, 0, 0, 1, 1, 1);
		t.startDrawingQuads();
		GL11.glDisable(GL11.GL_CULL_FACE);
		blockRender.renderFaceXNeg(Block.stone, -0.5D, -0.5D, -0.5D, Block.blockIron.getIcon(0, 0));
		blockRender.renderFaceXPos(Block.stone, -0.5D, -0.5D, -0.5D, Block.blockIron.getIcon(0, 0));
		blockRender.renderFaceYNeg(Block.stone, -0.5D, -0.5D, -0.5D, Block.blockIron.getIcon(0, 0));
		blockRender.renderFaceYPos(Block.stone, -0.5D, -0.5D, -0.5D, Block.blockIron.getIcon(0, 0));
		blockRender.renderFaceZNeg(Block.stone, -0.5D, -0.5D, -0.5D, Block.blockIron.getIcon(0, 0));
		blockRender.renderFaceZPos(Block.stone, -0.5D, -0.5D, -0.5D, Block.blockIron.getIcon(0, 0));
		t.draw();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	protected boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + scale && mouseY >= y && mouseY < y + scale ;
    }

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		super.mouseClickMove(mouseX, mouseY, button, time);
		int dx = mouseX - startClickX;
		int dy = mouseY - startClickY;
		rotX -= dy / 4;
		rotY += dx / 4;
		rotX = Math.min(20, Math.max(-20, rotX));
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button){
		super.mouseClicked(mouseX, mouseY, button);
		startClickX = mouseX;
		startClickY = mouseY;
	}
}
