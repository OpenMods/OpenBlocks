package openblocks.client.gui.component;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import openblocks.sync.SyncableInt;
import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

public class GuiComponentColorPicker extends BaseComponent {

	private GuiComponentSlider slider;
	private SyncableInt color;
	private int pointX = 0;
	private int pointY = 0;
	private SyncableInt tone = new SyncableInt();

	public GuiComponentColorPicker(int x, int y, SyncableInt color) {
		super(x, y);
		this.color = color;
		setFromColor(color.getValue());
		slider = new GuiComponentSlider(0, 55, 100, 0, 255, tone, false) {
			@Override
			public void onMouseUp() {
				calculateColor();
			}
		};
		addComponent(slider);
	}
	
	public SyncableInt getColor() {
		return color;
	}

	public void setFromColor(int col) {
		float[] hsb = new float[3];
		Color.RGBtoHSB((col & 0xFF0000) >> 16, (col & 0x00FF00) >> 8, col & 0x0000FF, hsb);
		pointX = (int)(hsb[0] * 100);
		pointY = (int)((1.0f - hsb[2]) * 50);
		tone.setValue(255 - (int)(hsb[1] * 255));
	}

	@Override
	public int getWidth() {
		return 100;
	}

	@Override
	public int getHeight() {
		return 70;
	}

	public int getColorsHeight() {
		return getHeight() - 20;
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		if (mouseY > getColorsHeight()) { return; }
		pointX = mouseX;
		pointY = mouseY;
		calculateColor();
	}

	public void calculateColor() {
		float h = pointX * 0.01f;
		float b = 1.0f - (pointY * 0.02f);
		float s = 1.0f - (tone.getValue() / 255f);
		int col = Color.HSBtoRGB(h, s, b) & 0xFFFFFF;
		color.setValue(col);
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glColor4f(1, 1, 1, 1);

		int renderX = offsetX + x;
		int renderY = offsetY + y;

		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		this.drawTexturedModalRect(renderX, renderY, 156, 206, getWidth(), getColorsHeight());
		this.drawRect(renderX, renderY, renderX + getWidth(), renderY
				+ getColorsHeight(), (tone.getValue() << 24) | 0xFFFFFF);

		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(0, 0, 0, 1.0f);
		tessellator.addVertex(renderX, (double)renderY + getColorsHeight(), 0.0D);
		tessellator.addVertex((double)renderX + getWidth(), (double)renderY
				+ getColorsHeight(), 0.0D);
		tessellator.setColorRGBA_F(0, 0, 0, 0f);
		tessellator.addVertex((double)renderX + getWidth(), renderY, 0.0D);
		tessellator.addVertex(renderX, renderY, 0.0D);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		this.drawRect(renderX + pointX - 1, renderY + pointY - 1, renderX
				+ pointX + 1, renderY + pointY + 1, 0xCCCC0000);
	}
}
