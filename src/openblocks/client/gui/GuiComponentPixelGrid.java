package openblocks.client.gui;

import java.util.Arrays;

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

	public GuiComponentPixelGrid(int x, int y, int cols, int rows, int scale, SyncableIntArray colorGrid, SyncableInt color) {
		super(x, y);
		this.colorGrid = colorGrid;
		this.cols = cols;
		this.rows = rows;
		this.scale = scale;
		this.color = color;
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
		drawAt(startX, startY);
		if (isMouseOver(mouseX, mouseY)) {
			drawAt(startX - getWidth(), startY);
			drawAt(startX + getWidth(), startY);
			drawAt(startX, startY + getHeight());
			drawAt(startX, startY - getHeight());
		}
	}
	
	private void drawAt(int x, int y) {
		int[] pixels = colorGrid.getValue();
		for (int r = 0, i = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++, i++) {
				int bX = x + (c * scale);
				int bY = y + (r * scale);
				drawRect(bX, bY, bX + scale, bY + scale, pixels[i]);
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
