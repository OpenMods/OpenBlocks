package openblocks.client.gui.component;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiComponentTabs extends Gui {
	
	protected int x;
	protected int y;
	protected Map<GuiComponentTab, Void> tabs;
	
	public GuiComponentTabs(int x, int y) {
		this.x = x;
		this.y = y;
		tabs = new HashMap<GuiComponentTab, Void>();
	}
	
	public void addTab(GuiComponentTab tab) {
		tabs.put(tab, null);
		tab.setX(this.x - 3);
	}
	
	public void render(Minecraft minecraft, int mouseX, int mouseY) {
		int offsetY = y;
		for (GuiComponentTab tab : tabs.keySet()) {
			tab.setY(offsetY);
			tab.render(minecraft, mouseX, mouseY);
			offsetY += tab.getHeight();
		}
	}
	
}
