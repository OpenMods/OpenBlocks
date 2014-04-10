package openblocks.client.gui;

import openblocks.common.container.ContainerCreativeItemSpawner;
import openblocks.common.tileentity.TileEntityCreativeItemSpawner;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.GuiComponentSlider;

public class GuiCreativeItemSpawner extends BaseGuiContainer<ContainerCreativeItemSpawner> {

	private GuiComponentSlider rangeSlider;
	private GuiComponentSlider delaySlider;
	private GuiComponentLabel rangeLabel;
	private GuiComponentLabel delayLabel;
	
	public GuiCreativeItemSpawner(ContainerCreativeItemSpawner container) {
		super(container, 176, 190, "openblocks.gui.itemspawner");
		
		TileEntityCreativeItemSpawner tile = container.getOwner();

		rangeLabel = new GuiComponentLabel(60, 25, "Range");
		rangeSlider = new GuiComponentSlider(60, 35, 100, 1, 100, tile.getRange());
		
		delaySlider = new GuiComponentSlider(60, 70, 100, 1, 200, tile.getDelay());
		delayLabel = new GuiComponentLabel(60, 60, "Delay (avg. Seconds)");
		
		root.addComponent(rangeSlider);
		root.addComponent(delaySlider);
		root.addComponent(rangeLabel);
		root.addComponent(delayLabel);
	}

}
