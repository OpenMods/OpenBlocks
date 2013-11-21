package openmods.client.gui.component;

import net.minecraft.client.Minecraft;
import openmods.network.sync.SyncableInt;
import openmods.utils.CompatibilityUtils;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiComponentSlider extends BaseComponent {

	private static final int HANDLE_SIZE = 8;

	private int width;
	private int min;
	private int max;
	private SyncableInt value;
	private double stepSize;
	private int steps;
	private boolean isDragging = false;
	private int startDragX;
	private boolean showValue = true;

	public GuiComponentSlider(int x, int y, int width, int min, int max, SyncableInt val, boolean showValue) {
		this(x, y, width, min, max, val);
		this.showValue = showValue;
	}

	public GuiComponentSlider(int x, int y, int width, int min, int max, SyncableInt val) {
		super(x, y);
		this.width = width;
		this.min = min;
		this.max = max;
		this.steps = max - min;
		this.value = val;
		this.stepSize = (double)(width - HANDLE_SIZE - 2) / (double)steps;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		int level = value.getValue();
		GL11.glColor4f(1, 1, 1, 1);
		int left = offsetX + x;
		int top = offsetY + y;
		int barStartX = left + 1;
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		drawTexturedModalRect(left, top, 0, 70, 1, getHeight());
		GL11.glPushMatrix();
		GL11.glTranslated(left + 1, top, 0);
		GL11.glScaled(getWidth() - 2, 1, 1);
		drawTexturedModalRect(0, 0, 1, 70, 1, getHeight());
		GL11.glPopMatrix();
		drawTexturedModalRect(left + getWidth() - 1, top, 2, 70, 1, getHeight());
		int handleX = (int)Math.floor(barStartX + stepSize * (level - min));
		if (Mouse.isButtonDown(0)) {
			if (!isDragging && mouseX + offsetX > handleX
					&& mouseX + offsetX < handleX + 8 && mouseY > y
					&& mouseY < y + getHeight()) {
				isDragging = true;
				startDragX = mouseX - handleX;
			}
		} else {
			if (isDragging) {
				isDragging = false;
				onMouseUp();
			}
		}
		if (isDragging) {
			int offX = mouseX - barStartX - startDragX;
			level = min + (int)Math.round(offX / stepSize);
		}
		level = Math.max(min, Math.min(max, level));
		drawTexturedModalRect(handleX, top + 1, 3, 70, 9, 10);
		if (showValue) {
			String label = Integer.toString(level);
			int strWidth = minecraft.fontRenderer.getStringWidth(label);
			minecraft.fontRenderer.drawString(label, handleX + 4
					- (strWidth / 2), top + 15, 4210752);
		}
		value.setValue(level);

	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return 12;
	}

	public void onMouseUp() {

	}
}
