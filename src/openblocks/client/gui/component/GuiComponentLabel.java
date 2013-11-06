package openblocks.client.gui.component;

import org.lwjgl.opengl.GL11;

import openblocks.sync.SyncableString;
import net.minecraft.client.Minecraft;

public class GuiComponentLabel extends BaseComponent {

	private String text;
	private SyncableString textObj;
	private float scale = 1f;

	public GuiComponentLabel(int x, int y, SyncableString txt) {
		this(x, y, txt.getValue());
		textObj = txt;
	}

	public GuiComponentLabel(int x, int y, String text) {
		super(x, y);
		this.text = text == null ? "" : text;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glPushMatrix();
		GL11.glTranslated(offsetX + x, offsetY + y, 1);
		GL11.glScalef(scale, scale, scale);
		minecraft.fontRenderer.drawString(textObj != null ? textObj.getValue() : text, 0, 0, 4210752);
		GL11.glPopMatrix();
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	public int getHeight() {
		return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
	}
	
	@Override
	public int getWidth() {
		return (int)(Minecraft.getMinecraft().fontRenderer.getStringWidth(textObj != null ? textObj.getValue() : text) * scale);
	}
}
