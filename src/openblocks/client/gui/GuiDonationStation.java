package openblocks.client.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import openblocks.client.gui.component.GuiComponentButton;
import openblocks.client.gui.component.GuiComponentLabel;
import openblocks.common.container.ContainerDonationStation;
import openblocks.common.tileentity.TileEntityDonationStation;

public class GuiDonationStation extends BaseGuiContainer<ContainerDonationStation> {

	private GuiComponentLabel labelModName;
	private GuiComponentLabel labelAuthors;
	private GuiComponentButton buttonDonate;
	
	public GuiDonationStation(ContainerDonationStation container){
		super(container, 176, 172, "openblocks.gui.donationstation");
		
		final TileEntityDonationStation station = container.getOwner();
		labelModName = new GuiComponentLabel(55, 31, station.getModName());
		labelAuthors = new GuiComponentLabel(55, 42, station.getAuthors());
		labelAuthors.setScale(0.5f);
		buttonDonate = new GuiComponentButton(31, 56, 115, 13, 0xFFFFFF, "Donate to the author") {
			@Override
			public void mouseClicked(int x, int y, int button) {
				super.mouseClicked(x, y, button);
				if (isButtonEnabled()) {
					try {
						Desktop.getDesktop().browse(URI.create(getContainer().getOwner().getDonateUrl()));
					} catch (IOException e) {
					}
				}
			}
		};
		
		panel.addComponent(labelModName);
		panel.addComponent(labelAuthors);
		panel.addComponent(buttonDonate);
	}

	public void preRender(float mouseX, float mouseY) {
		String donateUrl = getContainer().getOwner().getDonateUrl();
		buttonDonate.setButtonEnabled(donateUrl != null && !donateUrl.isEmpty());
	}
	
}
