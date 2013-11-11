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

	public interface IComponentListener {
		void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button);

		void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time);

		void componentMouseMove(BaseComponent component, int offsetX, int offsetY);

		void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button);
	}

	protected String name = null;
	protected int x;
	protected int y;
	protected boolean renderChildren = true;
	protected boolean enabled = true;
	private boolean hasMouse = false;

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

	public abstract int getWidth();

	public abstract int getHeight();

	public String getName() {
		return name;
	}

	public BaseComponent setName(String name) {
		this.name = name;
		return this;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Is this component currently capturing (seeing) the mouse.
	 * 
	 * @return true if the last isMouseOver call was true
	 */
	public boolean capturingMouse() {
		return hasMouse;
	}

	/**
	 * If the mouse position is inside this component
	 * 
	 * @param mouseX
	 *            X position relative from this components parent
	 * @param mouseY
	 *            Y position relative from this components parent
	 * @return true if the X and Y are inside this components area
	 */
	protected boolean isMouseOver(int mouseX, int mouseY) {
		return (hasMouse = mouseX >= x && mouseX < x + getWidth()
				&& mouseY >= y && mouseY < y + getHeight());
	}

	private List<IComponentListener> listeners = new ArrayList<IComponentListener>();
	public List<BaseComponent> components = new ArrayList<BaseComponent>();

	public BaseComponent addComponent(BaseComponent component) {
		components.add(component);
		return this;
	}

	public BaseComponent childByName(String componentName) {
		if (componentName == null) return null;
		for (BaseComponent component : components) {
			if (componentName.equals(component.getName())) { return component; }
		}
		return null;
	}

	public BaseComponent addListener(IComponentListener listener) {
		if (listeners.contains(listener)) return this;
		listeners.add(listener);
		return this;
	}

	public void removeListener(IComponentListener listener) {
		if (!listeners.contains(listener)) return;
		listeners.remove(listener);
	}

	public void clearListeners() {
		listeners.clear();
	}

	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (renderChildren) {
			for (BaseComponent component : components) {
				if (component != null && component.isEnabled()) {
					component.render(minecraft, offsetX + this.x, offsetY
							+ this.y, mouseX
							- this.x, mouseY - this.y);
				}
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		invokeListenersMouseDown(mouseX, mouseY, button);
		if (renderChildren) {
			for (BaseComponent component : components) {
				if (component != null && component.isEnabled()
						&& component.isMouseOver(mouseX, mouseY)) {
					component.mouseClicked(mouseX - component.x, mouseY
							- component.y, button);
				}
			}
		}
	}

	public void mouseClickMove(int mouseX, int mouseY, int button, /* love you */long time) {
		invokeListenersMouseDrag(mouseX, mouseY, button, time);
		if (renderChildren) {
			for (BaseComponent component : components) {
				if (component != null && component.isEnabled()
						&& component.isMouseOver(mouseX, mouseY)) {
					component.mouseClickMove(mouseX - component.x, mouseY
							- component.y, button, time);
				}
			}
		}
	}

	public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
		if (button >= 0) {
			invokeListenersMouseUp(mouseX, mouseY, button);
		} else {
			invokeListenersMouseMove(mouseX, mouseY);
		}
		if (renderChildren) {
			for (BaseComponent component : components) {
				if (component != null && component.isEnabled()
						&& component.isMouseOver(mouseX, mouseY)) {
					// Changed from mouseX - x, mouseY - y.
					// This could break some logic but I feel that is how it's
					// meant to be
					// Let me know if I've messed up - NC
					component.mouseMovedOrUp(mouseX - component.x, mouseY
							- component.y, button);
				}
			}
		}
	}

	/*
	 * The math on these methods is different because it takes the adjusted
	 * values
	 * from the handlers and passes it to listeners. No subtraction should be
	 * done here
	 */
	private void invokeListenersMouseDown(int offsetX, int offsetY, int button) {
		// If a handler was called from something that was a) not another
		// component
		// Or b) some external mod. The offsets might be derpy. So we still
		// check them
		// Even though 99% of the time they will be valid.
		if (isMouseOver(offsetX + x, offsetY + y)) {
			for (IComponentListener listener : listeners) {
				listener.componentMouseDown(this, offsetX, offsetY, button);
			}
		}
	}

	private void invokeListenersMouseDrag(int offsetX, int offsetY, int button, long time) {
		if (isMouseOver(offsetX + x, offsetY + y)) {
			for (IComponentListener listener : listeners) {
				listener.componentMouseDrag(this, offsetX, offsetY, button, time);
			}
		}
	}

	private void invokeListenersMouseMove(int offsetX, int offsetY) {
		if (isMouseOver(offsetX + x, offsetY + y)) {
			for (IComponentListener listener : listeners) {
				listener.componentMouseMove(this, offsetX, offsetY);
			}
		}
	}

	private void invokeListenersMouseUp(int offsetX, int offsetY, int button) {
		if (isMouseOver(offsetX + x, offsetY + y)) {
			for (IComponentListener listener : listeners) {
				listener.componentMouseDown(this, offsetX, offsetY, button);
			}
		}
	}
}
