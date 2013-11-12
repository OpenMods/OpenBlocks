package openblocks.client.gui.component;

import openblocks.sync.SyncableFloat;
import net.minecraft.client.Minecraft;

public class GuiComponentLevel extends BaseComponent {
	private int width;
	private int height;
	private int fColor;
	private int bColor;
	private float value;
	private float min, max;
	private SyncableFloat valueObj = null;

	public GuiComponentLevel(int x, int y, int width, int height, int levelColor, int backgroundColor, float min, float max, float value) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.fColor = levelColor;
		this.bColor = backgroundColor;
		this.min = min;
		this.max = max;
		this.value = value;
	}
	
	public GuiComponentLevel(int x, int y, int width, int height, int levelColor, int backgroundColor, float min, float max, SyncableFloat value) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.fColor = levelColor;
		this.bColor = backgroundColor;
		this.min = min;
		this.max = max;
		this.value = value.getValue();
		this.valueObj = value;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	public void setValue(float v) {
		this.value = v;
	}
	
	public float getValue() {
		if(valueObj != null) return valueObj.getValue();
		return value;
	}
	
	private float getFillHeight() {
		float value = getValue();
		if(value > max) value = max;
		if(value < min) value = min;
		float percent = value / max;
		return percent * getHeight();
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		int oX = x + offsetX;
		int oY = y + offsetY;
		// Fill with background
		drawRect(oX, oY, oX + width, oY + height, bColor);
		// Draw level
		drawRect(oX, oY + (height - (int)getFillHeight()), oX + width, oY + height, fColor);
	}
}
