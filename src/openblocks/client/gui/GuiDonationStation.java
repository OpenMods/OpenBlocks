package openblocks.client.gui;

import openblocks.client.gui.component.GuiComponentLabel;
import openblocks.common.container.ContainerDonationStation;
import openblocks.common.tileentity.TileEntityDonationStation;

public class GuiDonationStation extends BaseGuiContainer<ContainerDonationStation> {

	private GuiComponentLabel label;
	
	public GuiDonationStation(ContainerDonationStation container){
		super(container, 176, 152, "openblocks.gui.donationstation");
		
		TileEntityDonationStation station = container.getOwner();
		label = new GuiComponentLabel(68, 32, station.getModName());
		
		panel.addComponent(label);
	}

}
