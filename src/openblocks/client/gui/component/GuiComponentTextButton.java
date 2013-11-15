package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;

public class GuiComponentTextButton extends GuiComponentButton {

	private String text;

	public GuiComponentTextButton(int x, int y, int width, int height, int color, String text) {
		super(x, y, width, height, color);
		this.text = text;
	}

	public GuiComponentTextButton(int x, int y, int width, int height, int color) {
		this(x, y, width, height, color, "");
	}

	public GuiComponentTextButton setText(String buttonText) {
		this.text = buttonText;
		return this;
	}

	@Override
	public void renderContents(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY, boolean pressed) {
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
