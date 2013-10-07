package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

public class GuiComponentBox extends BaseComponent {

	protected int width;
	protected int height;
	protected int u;
	protected int v;
	protected int color;

	public GuiComponentBox(int x, int y, int width, int height, int u, int v, int color) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.u = u;
		this.v = v;
		this.color = color;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getColor() {
		return color;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void renderTopLeftCorner(int offsetX, int offsetY) {
		drawTexturedModalRect(offsetX + x, offsetY + y, u, v, 4, 4);
	}

	public void renderTopRightCorner(int offsetX, int offsetY) {
		drawTexturedModalRect(offsetX + x + getWidth() - 3, offsetY + y, u + 5, v, 3, 3);
	}

	public void renderBottomLeftCorner(int offsetX, int offsetY) {
		drawTexturedModalRect(offsetX + x, offsetY + y + getHeight() - 3, u + 11, v, 3, 3);
	}

	public void renderBottomRightCorner(int offsetX, int offsetY) {
		drawTexturedModalRect(offsetX + x + getWidth() - 4, offsetY + y + getHeight() - 4, u + 15, v, 4, 4);
	}

	public void renderBottomEdge(int offsetX, int offsetY) {
		GL11.glPushMatrix();
		GL11.glTranslated((double)offsetX + x + 3, (double)offsetY + y + getHeight() - 3, 0);
		GL11.glScaled((double)getWidth() - 6, 1, 0);
		drawTexturedModalRect(0, 0, u + 14, v, 1, 3);
		GL11.glPopMatrix();
	}

	public void renderTopEdge(int offsetX, int offsetY) {
		GL11.glPushMatrix();
		GL11.glTranslated((double)offsetX + x + 3, (double)offsetY + y, 0);
		GL11.glScaled((double)getWidth() - 6, 1, 0);
		drawTexturedModalRect(0, 0, u + 4, v, 1, 3);
		GL11.glPopMatrix();
	}

	public void renderLeftEdge(int offsetX, int offsetY) {
		GL11.glPushMatrix();
		GL11.glTranslated((double)offsetX + x, (double)offsetY + y + 3, 0);
		GL11.glScaled(1, getHeight() - 6, 0);
		drawTexturedModalRect(0, 0, u, v + 4, 3, 1);
		GL11.glPopMatrix();
	}

	public void renderRightEdge(int offsetX, int offsetY) {
		GL11.glPushMatrix();
		GL11.glTranslated(offsetX + x + getWidth() - 3, (double)offsetY + y + 3, 0);
		GL11.glScaled(1, getHeight() - 6, 0);
		drawTexturedModalRect(0, 0, u + 8, v, 3, 1);
		GL11.glPopMatrix();
	}

	public void renderBackground(int offsetX, int offsetY) {
		GL11.glPushMatrix();
		GL11.glTranslated(offsetX + x + 3, (double)offsetY + y + 3, 0);
		GL11.glScaled(getWidth() - 6, getHeight() - 6, 0);
		drawTexturedModalRect(0, 0, u + 19, v, 1, 1);
		GL11.glPopMatrix();
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		RenderHelper.disableStandardItemLighting();
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		int c = getColor();
		float r = (c >> 16 & 255) / 255.0F;
		float g = (c >> 8 & 255) / 255.0F;
		float b = (c & 255) / 255.0F;
		GL11.glColor4f(r, g, b, 1);
		renderBackground(offsetX, offsetY);
		renderTopEdge(offsetX, offsetY);
		renderBottomEdge(offsetX, offsetY);
		renderLeftEdge(offsetX, offsetY);
		renderRightEdge(offsetX, offsetY);

		renderTopLeftCorner(offsetX, offsetY);
		renderTopRightCorner(offsetX, offsetY);
		renderBottomLeftCorner(offsetX, offsetY);
		renderBottomRightCorner(offsetX, offsetY);
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
	}

	protected boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
}
