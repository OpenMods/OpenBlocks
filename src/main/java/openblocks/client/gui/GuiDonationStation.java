package openblocks.client.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import openblocks.common.container.ContainerDonationStation;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.GuiComponentTextButton;
import openmods.gui.listener.IMouseDownListener;

public class GuiDonationStation extends BaseGuiContainer<ContainerDonationStation> {

	private final int PROMPT_REPLY_ACTION = 0;
	private URI displayedURI = null;
	private GuiComponentTextButton buttonDonate;
	private GuiComponentLabel lblAuthors;

	public GuiDonationStation(ContainerDonationStation container) {
		super(container, 176, 172, "openblocks.gui.donationstation");

		root.addComponent(new GuiComponentLabel(55, 31, 100, 10, "")
				.setName("lblModName"));
		root.addComponent((lblAuthors = new GuiComponentLabel(55, 42, 200, 18, "").setScale(0.5f))
				.setName("lblAuthors"));
		root.addComponent((buttonDonate = new GuiComponentTextButton(31, 60, 115, 13, 0xFFFFFF))
				.setText("Donate to the author")
				.setName("btnDonate"));

		root.setListener(new IMouseDownListener() {
			@Override
			public void componentMouseDown(BaseComponent component, int x, int y, int button) {
				URI uri = URI.create(getContainer().getOwner().getDonateUrl());
				if (uri != null) {
					// Rude not to ask
					if (Minecraft.getMinecraft().gameSettings.chatLinksPrompt) {
						displayedURI = uri;
						mc.displayGuiScreen(new GuiConfirmOpenLink(GuiDonationStation.this, displayedURI.toString(), PROMPT_REPLY_ACTION, false));
					} else {
						openURI(uri);
					}
				}
			}
		});
	}

	@Override
	public void preRender(float mouseX, float mouseY) {
		super.preRender(mouseX, mouseY);
		String donateUrl = getContainer().getOwner().getDonateUrl();
		buttonDonate.setButtonEnabled(donateUrl != null && !donateUrl.isEmpty());
	}

	@Override
	public void postRender(int mouseX, int mouseY) {
		super.postRender(mouseX, mouseY);
		if (lblAuthors.isOverflowing()) {
			lblAuthors.setTooltip(lblAuthors.getFormattedText(fontRendererObj));
		} else {
			lblAuthors.clearTooltip();
		}
	}

	private void openURI(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {}
		getContainer().getOwner().showSomeLove();
	}

	@Override
	public void confirmClicked(boolean result, int action) {
		if (action == PROMPT_REPLY_ACTION && result) {
			openURI(this.displayedURI);
			this.displayedURI = null;
		}
		this.mc.displayGuiScreen(this);
	}
}
