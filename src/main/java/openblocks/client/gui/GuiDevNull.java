package openblocks.client.gui;

import openblocks.common.container.ContainerDevNull;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.BaseComposite;
import openmods.gui.component.GuiComponentPanel;

public class GuiDevNull extends BaseGuiContainer<ContainerDevNull> {

	public GuiDevNull(ContainerDevNull container) {
		super(container, 176, 137, "item.openblocks.dev_null.name");
	}

	@Override
	protected BaseComposite createRoot() {
		GuiComponentPanel panel = new GuiComponentPanel(0, 0, xSize, ySize, getContainer());
		panel.setSlotRenderer(0, GuiComponentPanel.bigSlot);
		return panel;
	}

}
