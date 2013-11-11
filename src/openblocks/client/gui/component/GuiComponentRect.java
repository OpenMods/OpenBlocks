package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;

public class GuiComponentRect extends BaseComponent {

	private int width;
	private int height;
	private int color;

	public GuiComponentRect(int x, int y, int width, int height, int color) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.color = color;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		int oX = x + offsetX;
		int oY = y + offsetY;
		drawRect(oX, oY, oX + width, oY + height, color);
	}

}
