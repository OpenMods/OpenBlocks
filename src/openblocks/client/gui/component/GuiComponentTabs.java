package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;

public class GuiComponentTabs extends BaseComponent {
	
	protected GuiComponentTab activeTab;
	
	public GuiComponentTabs(int x, int y) {
		super(x, y);
	}
	
	@Override
	public void addComponent(BaseComponent component) {
		super.addComponent(component);
		if (component instanceof GuiComponentTab) {
			((GuiComponentTab)component).setContainer(this);
		}
	}
	
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		int oY = 0;
		for (BaseComponent component : components) {
			if (component instanceof GuiComponentTab) {
				component.setY(oY);
				oY += ((GuiComponentTab)component).getHeight() - 1;
			}
		}
	}
	
	public void onTabClicked(GuiComponentTab tab) {
		if (tab != activeTab) {
			if (activeTab != null) {
				activeTab.setActive(false);
			}
			tab.setActive(true);
			activeTab = tab;
		}
	}
	
}
