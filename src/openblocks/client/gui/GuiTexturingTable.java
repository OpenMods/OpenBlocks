package openblocks.client.gui;

import openblocks.common.container.ContainerTexturingTable;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.GuiComponentColorPicker;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableIntArray;

public class GuiTexturingTable extends BaseGuiContainer<ContainerTexturingTable>{

	private GuiComponentColorPicker colorPicker;
	private GuiComponentPixelGrid pixelGrid;
	
	public GuiTexturingTable(ContainerTexturingTable container) {
		super(container, 176, 200, "openblocks.gui.texturingtable");
		
		SyncableInt color = container.getOwner().getClientColor();
		SyncableIntArray colorGrid = container.getOwner().getClientColorGrid();
		
		root.addComponent(pixelGrid = new GuiComponentPixelGrid(120, 60, 16, 16, 3, colorGrid, color));		
		root.addComponent(colorPicker = new GuiComponentColorPicker(10, 20, color));
	}

}
