package openblocks.client.gui;

import openblocks.common.container.ContainerDrawingTable;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.IComponentListener;

public class GuiDrawingTable extends BaseGuiContainer<ContainerDrawingTable>
		implements IComponentListener {

	public GuiDrawingTable(ContainerDrawingTable container) {
		super(container, 176, 172, "openblocks.gui.drawingtable");

	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {}

}
