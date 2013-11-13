package openblocks.client.gui.component;

import openblocks.sync.SyncableInt;

public class GuiComponentColorBox extends GuiComponentRect {

	private SyncableInt syncableColor;
	
	public GuiComponentColorBox(int x, int y, int width, int height, SyncableInt color) {
		super(x, y, width, height, 0xFF000000);
		this.syncableColor = color;
	}
	
	@Override
	public int getColorForRender() {
		int col = syncableColor.getValue();
		return col | (0xFF << 24);
	}

}
