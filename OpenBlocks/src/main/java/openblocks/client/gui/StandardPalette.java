package openblocks.client.gui;

public enum StandardPalette {
	blue(0x8784c8),
	lightblue(0x84c7c8),
	green(0x84c892),
	yellow(0xc7c884),
	red(0xc88a84),
	purple(0xc884bf);

	private int color;

	StandardPalette(int col) {
		this.color = col;
	}

	public int getColor() {
		return color;
	}
}