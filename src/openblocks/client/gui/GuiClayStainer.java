package openblocks.client.gui;

import openblocks.client.gui.component.GuiComponentColorPicker;
import openblocks.common.container.ContainerClayStainer;
import openblocks.common.tileentity.TileEntityClayStainer;

public class GuiClayStainer extends BaseGuiContainer<ContainerClayStainer> {

	private GuiComponentColorPicker colorPicker;
	
	public GuiClayStainer(ContainerClayStainer container) {
		super(container, 176, 190, "openblocks.gui.claystainer");
		
		TileEntityClayStainer stainer = container.getOwner();
		
		colorPicker = new GuiComponentColorPicker(10, 20, stainer.getColor(), stainer.getTone());
		
		panel.addComponent(colorPicker);
	}

}
