package openblocks.client.gui;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import openblocks.common.container.ContainerDonationStation;
import openblocks.common.tileentity.TileEntityDonationStation;
import openmods.Log;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.GuiComponentTextButton;
import openmods.gui.listener.IMouseDownListener;

public class GuiDonationStation extends BaseGuiContainer<ContainerDonationStation> {

	private final int PROMPT_REPLY_ACTION = 0;

	private URI displayedURI;
	private final GuiComponentLabel lblModName;
	private final GuiComponentTextButton buttonDonate;
	private final GuiComponentLabel lblAuthors;

	public GuiDonationStation(ContainerDonationStation container) {
		super(container, 176, 172, "openblocks.gui.donationstation");

		root.addComponent((lblModName = new GuiComponentLabel(55, 31, 100, 10, "")));
		root.addComponent((lblAuthors = new GuiComponentLabel(55, 42, 200, 18, "").setScale(0.5f)));
		root.addComponent((buttonDonate = new GuiComponentTextButton(31, 60, 115, 13, 0xFFFFFF)).setText("Donate to the author"));

		buttonDonate.setListener((IMouseDownListener)(component, x, y, button) -> {
			if (!buttonDonate.isButtonEnabled()) return;

			String donationUrl = getContainer().getOwner().getDonationUrl();
			if (Strings.isNullOrEmpty(donationUrl)) return;

			URI uri = URI.create(donationUrl);

			if (Minecraft.getMinecraft().gameSettings.chatLinksPrompt) {
				displayedURI = uri;
				mc.displayGuiScreen(new ConfirmOpenLinkScreen(GuiDonationStation.this, displayedURI.toString(), PROMPT_REPLY_ACTION, false));
			} else {
				openURI(uri);
			}
		});
	}

	@Override
	public void preRender(float mouseX, float mouseY) {
		super.preRender(mouseX, mouseY);
		final TileEntityDonationStation owner = getContainer().getOwner();

		if (owner.hasItem()) {
			String donateUrl = owner.getDonationUrl();
			buttonDonate.setButtonEnabled(!Strings.isNullOrEmpty(donateUrl));

			String name = owner.getModName();
			lblModName.setText(Strings.isNullOrEmpty(name)? "Vanilla / Unknown" : name);

			List<String> authors = owner.getModAuthors();
			lblAuthors.setText(authors == null? "" : Joiner.on(",").join(authors));
		} else {
			buttonDonate.setButtonEnabled(false);
			lblModName.setText("");
			lblAuthors.setText("");
		}
	}

	@Override
	public void postRender(int mouseX, int mouseY) {
		super.postRender(mouseX, mouseY);
		if (lblAuthors.isOverflowing()) {
			lblAuthors.setTooltip(lblAuthors.getFormattedText(fontRenderer));
		} else {
			lblAuthors.clearTooltip();
		}
	}

	private static void openURI(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			Log.info(e, "Failed to open uri %s", uri);
		}
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
