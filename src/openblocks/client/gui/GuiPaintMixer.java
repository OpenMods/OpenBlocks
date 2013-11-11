package openblocks.client.gui;

import openblocks.client.gui.component.GuiComponentColorPicker;
import openblocks.common.container.ContainerPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer;

public class GuiPaintMixer extends BaseGuiContainer<ContainerPaintMixer> {

	private GuiComponentColorPicker colorPicker;
	
	public GuiPaintMixer(ContainerPaintMixer container) {
		super(container, 176, 190, "openblocks.gui.claystainer");
		TileEntityPaintMixer mixer = container.getOwner();
		colorPicker = new GuiComponentColorPicker(10, 20, mixer.getColor());
		panel.addComponent(colorPicker);
	}

}
