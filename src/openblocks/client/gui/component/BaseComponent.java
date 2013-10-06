package openblocks.client.gui.component;

import java.util.HashSet;

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
	
	public HashSet<BaseComponent> components = new HashSet<BaseComponent>();
	
	public void addComponent(BaseComponent component) {
		components.add(component);
	}
	
	public void render(Minecraft minecraft, int x, int y) {
		if (renderChildren) {
			for (BaseComponent component : components) {
				component.render(minecraft, x, y);
			}
		}
	}

	public void mouseClicked(int x, int y, int button){
		if (renderChildren) {
			for (BaseComponent component : components) {
				component.mouseClicked(x, y, button);
			}
		}
	}
}
