package openblocks.client.gui;

import openblocks.client.gui.component.BaseComponent;
import openblocks.client.gui.component.GuiComponentLabel;
import openblocks.client.gui.component.BaseComponent.IComponentListener;
import openblocks.common.DonationUrlManager;
import openblocks.common.container.ContainerDonationStation;
import openblocks.common.tileentity.TileEntityDonationStation;

public class GuiDonationStation extends BaseGuiContainer<ContainerDonationStation> 
	implements IComponentListener {

	private GuiComponentLabel label;
	
	public GuiDonationStation(ContainerDonationStation container){
		super(container, 176, 152, "openblocks.gui.donationstation");
		
		final TileEntityDonationStation station = container.getOwner();
		
		// We could do this for our handlers
		// Note: Please always pass the event to the super.
		// But this creates UGLY Anonymous classes which are a nightmare to
		// Debug. (This one is GuiDonationStation$1 for example) -NC
		label = new GuiComponentLabel(68, 32, station.getModName()) {
			@Override
			public void mouseClicked(int mouseX, int mouseY, int button) {
				super.mouseClicked(mouseX, mouseY, button);
				System.out.println(DonationUrlManager.instance().getUrl(station.getModName().getValue()));
			}
		};
		// So I created a mini AWT in OpenBlocks :D
		// Hope you likey. -NC
		label.addListener(this);
		
		panel.addComponent(label);
	}

	// All coords passed around everywhere are relative to the component in question
	// So if there is a component argument involved, the coords are internal.
	// I fixed up all the components to behave like this.
	@Override
	public void componentMouseDown(BaseComponent component, int offsetX,
			int offsetY, int button) {
		System.out.println("Click on " + component.toString() + " at " + offsetX + "," + offsetY);
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX,
			int offsetY, int button, long time) {
		
	}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX,
			int offsetY) {
		
	}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX,
			int offsetY, int button) {
		
	}	

}
