package openblocks.client.gui;

import net.minecraft.item.ItemStack;
import openblocks.common.container.ContainerTexturingTable;
import openblocks.common.tileentity.TileEntityTexturingTable;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableIntArray;

public class GuiTexturingTable extends BaseGuiContainer<ContainerTexturingTable> implements IComponentListener{

	private GuiComponentColorPicker colorPicker;
	private GuiComponentPixelGrid pixelGrid;
	private GuiComponentTextButton buttonDraw;
	
	public GuiTexturingTable(ContainerTexturingTable container) {
		super(container, 176, 200, "openblocks.gui.texturingtable");
		
		SyncableInt color = container.getOwner().getClientColor();
		SyncableIntArray colorGrid = container.getOwner().getClientColorGrid();
		
		root.addComponent(pixelGrid = new GuiComponentPixelGrid(118, 56, 16, 16, 3, colorGrid, color));		
		root.addComponent(colorPicker = new GuiComponentColorPicker(10, 20, color));
		root.addComponent((buttonDraw = new GuiComponentTextButton(69, 91, 40, 13, 0xFFFFFF))
				.setText("Draw")
				.setName("btnDraw")
				.addListener(this));
	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		if (component.equals(buttonDraw)) {
			TileEntityTexturingTable table = getContainer().getOwner();
			table.sendColorsToServer();
		}
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {
		// TODO Auto-generated method stub
		
	}

}
