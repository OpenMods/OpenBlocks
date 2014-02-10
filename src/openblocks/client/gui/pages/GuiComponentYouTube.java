package openblocks.client.gui.pages;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentLabel;

public class GuiComponentYouTube extends BaseComponent {

	private final int PROMPT_REPLY_ACTION = 0;

	private GuiComponentLabel label;
	private String url;

	public static URI youtubeUrl;

	public GuiComponentYouTube(int x, int y, String url) {
		super(x, y);
		label = new GuiComponentLabel(0, 0, "Watch video");
		label.setScale(0.5f);
		this.url = url;
		addComponent(label);
	}

	@Override
	public int getWidth() {
		return label.getWidth();
	}

	@Override
	public int getHeight() {
		return 5;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		URI uri = URI.create(url);
		if (uri != null) {
			if (Minecraft.getMinecraft().gameSettings.chatLinksPrompt) {
				youtubeUrl = uri;
				Minecraft.getMinecraft().displayGuiScreen(new GuiConfirmOpenLink(Minecraft.getMinecraft().currentScreen, url, PROMPT_REPLY_ACTION, false));
			} else {
				openURI(uri);
			}
		}
	}

	public static void openURI(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {}
	}

}
