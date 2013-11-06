package openblocks.client.gui.component;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

public class GuiComponentButton extends GuiComponentBox {

	private String text;
	protected boolean buttonEnabled = true;
	
	public GuiComponentButton(int x, int y, int width, int height, int color, String text) {
		super(x, y, width, height, 0, 10, color);
		this.text = text;
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
		this.u = buttonEnabled ? (pressed ? 20 : 0) : 40;
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		int textWidth = minecraft.fontRenderer.getStringWidth(text);
		int offX = ((width - textWidth) / 2) + 1;
		int offY = 3;
		if (buttonEnabled && pressed) {
			offY++;
			offX++;
		}
		minecraft.fontRenderer.drawString(text, offsetX + x + offX, offsetY + y + offY, 4210752);
	}

}
