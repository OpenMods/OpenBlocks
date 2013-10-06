package openblocks.client.gui.component;

import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;

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
	
	public void renderTopLeftCorner(){
		drawTexturedModalRect(x, y, u, v, 4, 4);
	}
	
	public void renderTopRightCorner(){
		drawTexturedModalRect(x + getWidth() - 3, y, u + 5, v, 3, 3);
	}
	
	public void renderBottomLeftCorner(){
		drawTexturedModalRect(x, y + getHeight() - 3, u + 11, v, 3, 3);
	}
	
	public void renderBottomRightCorner(){
		drawTexturedModalRect(x + getWidth() - 4, y + getHeight() - 4, u + 15, v, 4, 4);
	}
	
	public void renderBottomEdge() {
		GL11.glPushMatrix();
		GL11.glTranslated((double)x + 3, (double)y + getHeight() - 3, 0);
		GL11.glScaled((double)getWidth() - 6, 1, 0);
		drawTexturedModalRect(0, 0, u+14, v, 1, 3);
		GL11.glPopMatrix();
	}
	
	public void renderTopEdge() {
		GL11.glPushMatrix();
		GL11.glTranslated((double)x + 3, (double)y, 0);
		GL11.glScaled((double)getWidth() - 6, 1, 0);
		drawTexturedModalRect(0, 0, u + 4, v, 1, 3);
		GL11.glPopMatrix();
	}
	
	public void renderLeftEdge() {
		GL11.glPushMatrix();
		GL11.glTranslated((double)x, (double)y + 3, 0);
		GL11.glScaled((double)1, getHeight() - 6, 0);
		drawTexturedModalRect(0, 0, u, v + 4, 3, 1);
		GL11.glPopMatrix();
	}
	
	public void renderRightEdge() {
		GL11.glPushMatrix();
		GL11.glTranslated((double)(x + getWidth() - 3), (double)y + 3, 0);
		GL11.glScaled((double)1, getHeight() - 6, 0);
		drawTexturedModalRect(0, 0, u + 8, v, 3, 1);
		GL11.glPopMatrix();
	}


	public void renderBackground() {
		GL11.glPushMatrix();
		GL11.glTranslated((double)(x + 3), (double)y + 3, 0);
		GL11.glScaled(getWidth() - 6, getHeight() - 6, 0);
		drawTexturedModalRect(0, 0, u + 19, v, 1, 1);
		GL11.glPopMatrix();
	}
	
	public void render(Minecraft minecraft, int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		int c = getColor();
        float r = (float)(c >> 16 & 255) / 255.0F;
        float g = (float)(c >> 8 & 255) / 255.0F;
        float b = (float)(c & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1);
		renderBackground();
		renderTopEdge();
		renderBottomEdge();
		renderLeftEdge();
		renderRightEdge();

		renderTopLeftCorner();
		renderTopRightCorner();
		renderBottomLeftCorner();
		renderBottomRightCorner();
		super.render(minecraft, mouseX, mouseY);
	}
	
	protected boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width &&  mouseY >= y && mouseY < y + height;
    }
}
