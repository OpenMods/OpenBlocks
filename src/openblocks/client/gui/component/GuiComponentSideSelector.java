package openblocks.client.gui.component;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Vec3;
import openblocks.utils.SidePicker;

import org.lwjgl.opengl.GL11;

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
		GL11.glRotated(rotX, 1, 0, 0);
		GL11.glRotated(rotY, 0, 1, 0);
		GL11.glColor4f(1, 1, 1, 1);
		minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		blockRender.setRenderBounds(0, 0, 0, 1, 1, 1);
		t.startDrawingQuads();
		GL11.glDisable(GL11.GL_CULL_FACE);

		t.setColorOpaque_F(0, 1, 1);
		blockRender.renderFaceXNeg(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));
		t.setColorOpaque_F(1, 0, 0);
		blockRender.renderFaceXPos(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));
		t.setColorOpaque_F(1, 0, 1);
		blockRender.renderFaceYNeg(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));
		t.setColorOpaque_F(0, 1, 0);
		blockRender.renderFaceYPos(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));
		t.setColorOpaque_F(1, 1, 0);
		blockRender.renderFaceZNeg(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));
		t.setColorOpaque_F(0, 0, 1);
		blockRender.renderFaceZPos(Block.stone, -0.5, -0.5, -0.5, Block.blockIron.getIcon(0, 0));
		t.draw();

		GL11.glPointSize(10);

		Map<SidePicker.Side, Vec3> hits = new SidePicker(0.5).calculateMouseHits();

		if (!hits.isEmpty()) {
			GL11.glBegin(GL11.GL_POINTS);
			System.out.println("++++++HITS++++++");
			for (Map.Entry<SidePicker.Side, Vec3> e : hits.entrySet()) {
				System.out.println(e.getKey());
				switch (e.getKey()) {
					case XPos:
						GL11.glColor3f(1, 0, 0);
						break;
					case YPos:
						GL11.glColor3f(0, 1, 0);
						break;
					case ZPos:
						GL11.glColor3f(0, 0, 1);
						break;
					case XNeg:
						GL11.glColor3f(0, 1, 1);
						break;
					case YNeg:
						GL11.glColor3f(1, 0, 1);
						break;
					case ZNeg:
						GL11.glColor3f(1, 1, 0);
						break;
				}
				Vec3 hit = e.getValue();
				GL11.glVertex3d(hit.xCoord, hit.yCoord, hit.zCoord);
			}
			GL11.glEnd();
		}

		/*
		 * HitCoord coord = new SidePicker(0.5).getNearestHit();
		 * if (coord != null)
		 * System.out.println(coord.side);
		 */

		GL11.glEnable(GL11.GL_CULL_FACE);

		GL11.glPopMatrix();
	}

	protected boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + scale && mouseY >= y && mouseY < y + scale;
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
	public void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		startClickX = mouseX;
		startClickY = mouseY;
	}
}
