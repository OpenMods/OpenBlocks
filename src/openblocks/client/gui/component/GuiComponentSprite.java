package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import openblocks.utils.FakeIcon;

import org.lwjgl.opengl.GL11;

public class GuiComponentSprite extends BaseComponent {

	private Icon icon;
	private ResourceLocation texture;
	private float r = 1, g = 1, b = 1;
	
	
	public static class Sprites {
		public static Icon hammer = FakeIcon.createSheetIcon(0, 233, 23, 23);
		public static Icon plus = FakeIcon.createSheetIcon(23, 242, 13, 13);
		public static Icon result = FakeIcon.createSheetIcon(36, 241, 22, 15);
	}

	public GuiComponentSprite(int x, int y, Icon icon, ResourceLocation texture) {
		super(x, y);
		this.texture = texture;
		this.icon = icon;
	}
	
	public GuiComponentSprite setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		if (icon == null) {
			return;
		}
		if (texture != null) minecraft.renderEngine.bindTexture(texture);
		GL11.glColor3f(r,g,b);
		this.drawTexturedModelRectFromIcon(offsetX + x, offsetY + y, icon, icon.getIconWidth(), icon.getIconHeight());
	}

	@Override
	public int getWidth() {
		if (icon != null) {
			return icon.getIconWidth();
		}
		return 0;
	}

	@Override
	public int getHeight() {
		if (icon != null) {
			return icon.getIconHeight();
		}
		return 0;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}
}
