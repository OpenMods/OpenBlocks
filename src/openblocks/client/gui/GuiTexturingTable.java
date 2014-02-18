package openblocks.client.gui;

import openblocks.common.container.ContainerTexturingTable;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.GuiComponentColorPicker;
import openmods.sync.SyncableInt;

public class GuiTexturingTable extends BaseGuiContainer<ContainerTexturingTable>{

	private GuiComponentColorPicker colorPicker;
	private GuiComponentPixelGrid pixelGrid;
	private SyncableInt color = new SyncableInt();
	
	public GuiTexturingTable(ContainerTexturingTable container) {
		super(container, 200, 200, "openblocks.gui.texturingtable");
		
		root.addComponent(pixelGrid = new GuiComponentPixelGrid(120, 60, 16, 16, 3, color));		
		root.addComponent(colorPicker = new GuiComponentColorPicker(10, 20, color));
	}

}
