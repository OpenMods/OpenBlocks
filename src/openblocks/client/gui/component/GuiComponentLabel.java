package openblocks.client.gui.component;

import openblocks.sync.SyncableString;
import net.minecraft.client.Minecraft;

public class GuiComponentLabel extends BaseComponent {

	private String text;
	private SyncableString textObj;

	public GuiComponentLabel(int x, int y, SyncableString txt) {
		this(x, y, txt.getValue());
		textObj = txt;
	}
	
	public GuiComponentLabel(int x, int y, String text) {
		super(x, y);
		this.text = text;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		minecraft.fontRenderer.drawString(textObj != null ? textObj.getValue() : text, offsetX + x, offsetY + y, 4210752);
	}

}
