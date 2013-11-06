package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

public class GuiComponentSprite extends BaseComponent {

	public static enum Sprite {

		hammer(0, 233, 23, 23),
		plus(23, 242, 13, 13),
		result(36, 241, 22, 15);

		private int u;
		private int v;
		private int width;
		private int height;

		Sprite(int u, int v, int width, int height) {
			this.u = u;
			this.v = v;
			this.width = width;
			this.height = height;
		}

		public int getU() {
			return u;
		}

		public int getV() {
			return v;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

	private Sprite sprite;

	public GuiComponentSprite(int x, int y, Sprite sprite) {
		super(x, y);
		this.sprite = sprite;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		GL11.glColor3f(1, 1, 1);
		drawTexturedModalRect(offsetX + x, offsetY + y, sprite.u, sprite.v, sprite.width, sprite.height);
	}

	@Override
	public int getWidth() {
		return sprite.getWidth();
	}

	@Override
	public int getHeight() {
		return sprite.getHeight();
	}
}
