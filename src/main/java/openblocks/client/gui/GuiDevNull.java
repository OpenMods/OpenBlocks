package openblocks.client.gui;

import openblocks.common.container.ContainerDevNull;
import openmods.gui.BaseGuiContainer;
import openmods.gui.IComponentParent;
import openmods.gui.component.BaseComposite;
import openmods.gui.component.GuiComponentPanel;

public class GuiDevNull extends BaseGuiContainer<ContainerDevNull> {

	public GuiDevNull(ContainerDevNull container) {
		super(container, 176, 137, "item.openblocks.devnull.name");
	}

	@Override
	protected BaseComposite createRoot(IComponentParent parent) {
		GuiComponentPanel panel = new GuiComponentPanel(parent, 0, 0, xSize, ySize, getContainer());
		panel.setSlotRenderer(0, GuiComponentPanel.bigSlot);
		return panel;
	}

}
