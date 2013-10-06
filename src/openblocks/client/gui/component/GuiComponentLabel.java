package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;

public class GuiComponentLabel extends BaseComponent {

	private String text;
	
	public GuiComponentLabel(int x, int y, String text) {
		super(x, y);
		this.text = text;
	}

	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		minecraft.fontRenderer.drawString(text, offsetX + x, offsetY + y, 4210752);
	}

}
