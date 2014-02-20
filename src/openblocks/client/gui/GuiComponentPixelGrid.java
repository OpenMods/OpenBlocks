package openblocks.client.gui;

import net.minecraft.client.Minecraft;
import openmods.gui.component.BaseComponent;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableIntArray;

class GuiComponentPixelGrid extends BaseComponent {

	private int cols;
	private int rows;
	private int scale;
	private SyncableInt color;
	private SyncableIntArray colorGrid;
	private int opacity = 0x0;

	public GuiComponentPixelGrid(int x, int y, int cols, int rows, int scale, SyncableIntArray colorGrid, SyncableInt color) {
		super(x, y);
		this.colorGrid = colorGrid;
		this.cols = cols;
		this.rows = rows;
		this.scale = scale;
		this.color = color;
	}

	public void setColors(int[] data) {
			this.colorGrid.setValue(data);
	}
	
	@Override
	public int getWidth() {
		return cols * scale;
	}

	@Override
	public int getHeight() {
		return rows * scale;
	}

	@Override
	public void renderOverlay(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderOverlay(minecraft, offsetX, offsetY, mouseX, mouseY);
		int startX = x + offsetX;
		int startY = y + offsetY;
		drawAt(startX, startY, 0xFF);
		if (isMouseOver(mouseX, mouseY)) {
			opacity += 0x4;
		} else {
			opacity -= 0x2;
		}
		if (opacity > 0xFF) {
			opacity = 0xFF;
		}
		if (opacity < 0) {
			opacity = 0;
		}
		if (opacity > 0) {
			drawAt(startX - getWidth(), startY, opacity);
			drawAt(startX + getWidth(), startY, opacity);
			drawAt(startX, startY + getHeight(), opacity);
			drawAt(startX, startY - getHeight(), opacity);
		}
	}
	
	private void drawAt(int x, int y, int opacity) {
		int[] pixels = colorGrid.getValue();
		for (int r = 0, i = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++, i++) {
				int bX = x + (c * scale);
				int bY = y + (r * scale);
				drawRect(bX, bY, bX + scale, bY + scale, ((pixels[i] & 0x00FFFFFF) | (opacity << 24)));
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		drawPixels(mouseX, mouseY);
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int button, /* love you */long time) {
		super.mouseClickMove(mouseX, mouseY, button, time);
		drawPixels(mouseX, mouseY);
	}
	
	private void drawPixels(int mouseX, int mouseY) {
		if (isMouseOver(mouseX + x, mouseY + y)) {
			colorGrid.setValue((mouseY / scale) * cols + (mouseX / scale), color.getValue() | (0xFF << 24));
		}
	}
}
