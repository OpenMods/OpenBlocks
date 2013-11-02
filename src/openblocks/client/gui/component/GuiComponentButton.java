package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Mouse;

public abstract class GuiComponentButton extends GuiComponentBox {

	protected boolean buttonEnabled = true;

	public GuiComponentButton(int x, int y, int width, int height, int color) {
		super(x, y, width, height, 0, 10, color);
	}

	public void setButtonEnabled(boolean enabled) {
		this.buttonEnabled = enabled;
	}

	public boolean isButtonEnabled() {
		return buttonEnabled;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		boolean pressed = isMouseOver(mouseX, mouseY) && Mouse.isButtonDown(0);
		this.u = buttonEnabled? (pressed? 20 : 0) : 40;
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		renderContents(minecraft, offsetX, offsetY, mouseX, mouseY, pressed);
	}

	protected abstract void renderContents(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY, boolean pressed);

}
