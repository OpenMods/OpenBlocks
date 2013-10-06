package openblocks.client.gui.component;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;

public class GuiComponentTab extends GuiComponentBox {

	protected GuiComponentTabs container;
	protected int expandedWidth;
	protected int expandedHeight;
	private boolean active = false;
	private Icon icon;
	
	public GuiComponentTab(int color, Icon icon, int expandedWidth, int expandedHeight) {
		super(0, 0, 24, 24, 0, 5, color);
		this.expandedWidth = expandedWidth;
		this.expandedHeight = expandedHeight;
		this.icon = icon;
	}

	@Override
	public void renderTopLeftCorner(int offsetX, int offsetY){
	}

	@Override
	public void renderBottomLeftCorner(int offsetX, int offsetY){
	}
	
	@Override
	public void renderLeftEdge(int offsetX, int offsetY) {
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		int targetWidth = active ? expandedWidth : 24;
		int targetHeight = active ? expandedHeight : 24;
		if (width != targetWidth) {
			width += (targetWidth-width)/2;
		}
		if (height != targetHeight) {
			height += (targetHeight-height)/2;
		}
		renderChildren = active;
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glColor4f(1, 1, 1, 1);
        RenderHelper.enableGUIStandardItemLighting();
		minecraft.renderEngine.bindTexture(TextureMap.locationItemsTexture);
		drawTexturedModelRectFromIcon(offsetX+x+3, offsetY+y+3, icon, 16, 16);
	}

	@Override
	public void mouseClicked(int x, int y, int button){
		if (isMouseOver(x, y)) {
			container.onTabClicked(this);
		}
	}
	
	public void setContainer(GuiComponentTabs container) {
		this.container = container;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
