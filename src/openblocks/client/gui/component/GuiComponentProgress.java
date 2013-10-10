package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

public class GuiComponentProgress extends BaseComponent {

	private double progress;

	public GuiComponentProgress(int x, int y) {
		super(x, y);
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		GL11.glColor3f(1, 1, 1);
		drawTexturedModalRect(offsetX + x, offsetY + y, 0, 38, 29, 12);
		int pxProgress = (int)Math.round(29 * progress);
		drawTexturedModalRect(offsetX + x, offsetY + y, 0, 50, pxProgress, 12);
	}
}
