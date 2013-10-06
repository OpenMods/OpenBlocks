package openblocks.client.gui.component;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class BaseComponent extends Gui {

	protected int x;
	protected int y;
	protected boolean renderChildren = true;
	
	public BaseComponent(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public List<BaseComponent> components = new ArrayList<BaseComponent>();
	
	public void addComponent(BaseComponent component) {
		components.add(component);
	}
	
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (renderChildren) {
			for (BaseComponent component : components) {
				component.render(minecraft, offsetX + this.x, offsetY + this.y, mouseX - this.x, mouseY - this.y);
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (renderChildren) {
			for (BaseComponent component : components) {
				component.mouseClicked(mouseX - x, mouseY - y, button);
			}
		}
	}
	public void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		if (renderChildren) {
			for (BaseComponent component : components) {
				component.mouseClickMove(mouseX - x, mouseY - y, button, time);
			}
		}
	}
}
