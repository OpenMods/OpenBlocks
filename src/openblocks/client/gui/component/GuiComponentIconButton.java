package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

public class GuiComponentIconButton extends GuiComponentButton {

	private ResourceLocation texture;
	private final Icon icon;

	public GuiComponentIconButton(int x, int y, int color, Icon icon) {
		super(x, y, icon.getIconWidth() + 4, icon.getIconHeight() + 4, color);
		this.icon = icon;
	}

	public GuiComponentIconButton(int x, int y, int color, Icon icon, ResourceLocation texture) {
		this(x, y, color, icon);
		this.texture = texture;
	}

	@Override
	public void renderContents(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY, boolean pressed) {
		if (texture != null) minecraft.renderEngine.bindTexture(texture);

		int offset = (buttonEnabled && pressed)? 3 : 2;

		drawTexturedModelRectFromIcon(offsetX + x + offset, offsetY + y + offset, icon, icon.getIconWidth(), icon.getIconHeight());
	}

}
