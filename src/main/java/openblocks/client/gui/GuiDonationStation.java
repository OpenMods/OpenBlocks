package openblocks.client.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import openblocks.common.container.ContainerDonationStation;
import openblocks.common.tileentity.TileEntityDonationStation;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;

public class GuiDonationStation extends
		BaseGuiContainer<ContainerDonationStation>
		implements IComponentListener {

	private final int PROMPT_REPLY_ACTION = 0;
	private URI displayedURI = null;
	GuiComponentTextButton buttonDonate;
	GuiComponentLabel lblAuthors;

	public GuiDonationStation(ContainerDonationStation container) {
		super(container, 176, 172, "openblocks.gui.donationstation");

		final TileEntityDonationStation station = container.getOwner();
		root.addComponent(new GuiComponentLabel(55, 31, 100, 10, station.getModName())
				.setName("lblModName"));
		root.addComponent((lblAuthors = new GuiComponentLabel(55, 42, 200, 18, station.getAuthors()).setScale(0.5f))
				.setName("lblAuthors"));
		root.addComponent((buttonDonate = new GuiComponentTextButton(31, 60, 115, 13, 0xFFFFFF))
				.setText("Donate to the author")
				.setName("btnDonate")
				.addListener(this));
	}

	@Override
	public void preRender(float mouseX, float mouseY) {
		super.preRender(mouseX, mouseY);
		String donateUrl = getContainer().getOwner().getDonateUrl();
		buttonDonate.setButtonEnabled(donateUrl != null && !donateUrl.isEmpty());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void postRender(int mouseX, int mouseY) {
		super.postRender(mouseX, mouseY);
		if (lblAuthors.capturingMouse()) {
			List lines = fontRendererObj.listFormattedStringToWidth(lblAuthors.getText(), 150);
			if (lines.size() < lblAuthors.getMaxLines()) return;
			drawHoveringText(lines, mouseX - this.guiLeft, mouseY - this.guiTop, fontRendererObj);
		}
	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		if (component.getName().equals("btnDonate")) {
			if (((GuiComponentTextButton)component).isButtonEnabled()) {
				URI uri = URI.create(getContainer().getOwner().getDonateUrl());
				if (uri != null) {
					// Rude not to ask
					if (Minecraft.getMinecraft().gameSettings.chatLinksPrompt) {
						this.displayedURI = uri;
						this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, this.displayedURI.toString(), PROMPT_REPLY_ACTION, false));
					} else {
						openURI(uri);
					}
				}
			}
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

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {}

}
