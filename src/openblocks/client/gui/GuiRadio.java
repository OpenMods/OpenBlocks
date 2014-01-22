package openblocks.client.gui;

import openblocks.common.container.ContainerRadio;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.GuiComponentTextbox;

public class GuiRadio extends BaseGuiContainer<ContainerRadio> {

	private GuiComponentTextbox textbox;

	public GuiRadio(ContainerRadio container) {
		super(container, 176, 151, "radio");

		root.addComponent(textbox = new GuiComponentTextbox(35, 25, 128, 10)
				.setText(container.getOwner().getUrl().getValue()));

	}

}
