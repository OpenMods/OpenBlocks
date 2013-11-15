package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import openblocks.sync.SyncableString;

import org.lwjgl.opengl.GL11;

public class GuiComponentLabel extends BaseComponent {

	private String text;
	private SyncableString textObj;
	private float scale = 1f;
	private String textDelta;
	private String[] formattedText;
	private int maxHeight, maxWidth;

	private static FontRenderer getFontRenderer() {
		return Minecraft.getMinecraft().fontRenderer;
	}

	public GuiComponentLabel(int x, int y, int width, int height, SyncableString txt) {
		this(x, y, width, height, txt.getValue());
		textObj = txt;
	}

	public GuiComponentLabel(int x, int y, String text) {
		this(x, y, getFontRenderer().getStringWidth(text), getFontRenderer().FONT_HEIGHT, text);
	}

	public GuiComponentLabel(int x, int y, SyncableString txt) {
		this(x, y, txt.getValue());
		textObj = txt;
	}

	public GuiComponentLabel(int x, int y, int width, int height, String text) {
		super(x, y);
		this.text = text;
		this.formattedText = new String[10];
		setMaxHeight(height);
		setMaxWidth(width);
	}

	@SuppressWarnings("unchecked")
	private void compileFormattedText(FontRenderer fr) {
		if (textDelta != null && textDelta.equals(getText())) return;
		textDelta = getText();
		if (textDelta == null) return;
		formattedText = (String[])fr.listFormattedStringToWidth(textDelta, maxWidth).toArray(formattedText);
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		if (getMaxHeight() < minecraft.fontRenderer.FONT_HEIGHT) return;
		if (getMaxWidth() < minecraft.fontRenderer.getCharWidth('m')) return;
		GL11.glPushMatrix();
		GL11.glTranslated(offsetX + x, offsetY + y, 1);
		GL11.glScalef(scale, scale, scale);
		compileFormattedText(minecraft.fontRenderer);
		int offset = 0;
		int lineCount = 0;
		for (String s : formattedText) {
			if (s == null) break;
			minecraft.fontRenderer.drawString(s, 0, offset, 4210752);
			offset += minecraft.fontRenderer.FONT_HEIGHT;
			if (++lineCount >= getMaxLines()) break;
		}
		GL11.glPopMatrix();
	}

	private int calculateHeight() {
		FontRenderer fr = getFontRenderer();
		compileFormattedText(fr);
		int offset = 0;
		int lineCount = 0;
		for (String s : formattedText) {
			if (s == null) break;
			offset += fr.FONT_HEIGHT;
			if (++lineCount >= getMaxLines()) break;
		}
		return offset;
	}

	private int calculateWidth() {
		FontRenderer fr = getFontRenderer();
		compileFormattedText(fr);
		float maxWidth = 0;
		for (String s : formattedText) {
			if (s == null) break;
			float width = fr.getStringWidth(s);
			if (width > maxWidth) maxWidth = width;
		}
		return (int)maxWidth;
	}

	public GuiComponentLabel setScale(float scale) {
		this.scale = scale;
		return this;
	}

	public float getScale() {
		return scale;
	}

	public GuiComponentLabel setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public GuiComponentLabel setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public int getMaxLines() {
		return (int)Math.floor(getMaxHeight() / scale
				/ getFontRenderer().FONT_HEIGHT);
	}

	public int getMaxWidth() {
		return this.maxWidth;
	}

	@Override
	public int getHeight() {
		return (int)(Math.min(getMaxHeight(), calculateHeight()) + 0.5);
	}

	@Override
	public int getWidth() {
		return (int)(Math.min(getMaxWidth(), calculateWidth()) + 0.5);
	}

	public String getText() {
		String pre = (textObj != null? textObj.getValue() : text);
		return pre == null? "" : pre;
	}
}
