package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiComponentTab extends GuiComponentBox {
	
	private String name;
	private boolean active;
	
	public GuiComponentTab(int x, int y, String name) {
		super(x, y, Minecraft.getMinecraft().fontRenderer.getStringWidth(name) + 20, 22, 0, 5, 0xc6c6c6);
		this.name = name;
	}
	
	@Override
	public void render(Minecraft minecraft, int mouseX, int mouseY) {
		super.render(minecraft, mouseX, mouseY);
		minecraft.fontRenderer.drawString(name, x + 10, y + 6, 4210752);
	}
	
	@Override
	public void renderBottomEdge() {
		
	}

	public void renderBottomLeftCorner(){
	}
	
	public void renderBottomRightCorner(){
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	@Override
	public int getHeight() {
		return active ? height : height - 2;
	}
	
	@Override
	public int getColor() {
		return active ? color : 0xa7a7a7;
	}
}
