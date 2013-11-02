package openblocks.client.gui.component;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class BaseComponent extends Gui {

	public enum TabColor {
		blue(0x8784c8),
		lightblue(0x84c7c8),
		green(0x84c892),
		yellow(0xc7c884),
		red(0xc88a84),
		purple(0xc884bf);

		private int color;

		TabColor(int col) {
			this.color = col;
		}

		public int getColor() {
			return color;
		}
	}

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
				if (component != null) {
					component.render(minecraft, offsetX + this.x, offsetY + this.y, mouseX
							- this.x, mouseY - this.y);
				}
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (renderChildren) {
			for (BaseComponent component : components) {
				if (component != null) {
					component.mouseClicked(mouseX - x, mouseY - y, button);
				}
			}
		}
	}

	public void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		if (renderChildren) {
			for (BaseComponent component : components) {
				if (component != null) {
					component.mouseClickMove(mouseX - x, mouseY - y, button, time);
				}
			}
		}
	}

	public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
		if (renderChildren) {
			for (BaseComponent component : components) {
				if (component != null) {
					component.mouseMovedOrUp(mouseX - x, mouseY - y, button);
				}
			}
		}
	}
}
