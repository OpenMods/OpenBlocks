package openblocks.client.gui;

public enum StandardPalette {
	BLUE(0x8784c8),
	LIGHT_BLUE(0x84c7c8),
	GREEN(0x84c892),
	YELLOW(0xc7c884),
	RED(0xc88a84),
	PURPLE(0xc884bf);

	public final int color;

	StandardPalette(int col) {
		this.color = col;
	}
}