package openblocks.client.gui;

import openblocks.common.container.ContainerLuggage;

public class GuiLuggage extends BaseGuiContainer<ContainerLuggage> {

	public GuiLuggage(ContainerLuggage container) {
		super(container, 176, container.luggage.isSpecial()? 221 : 167, "openblocks.gui.luggage");
	}

}
