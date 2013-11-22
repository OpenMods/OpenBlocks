package openblocks.client.gui;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.gui.component.BaseComponent.TabColor;

public class GuiVacuumHopper extends BaseGuiContainer<ContainerVacuumHopper> {

	private GuiComponentTankLevel xpLevel;
	private GuiComponentTabs tabs;
	private GuiComponentSideSelector xpSideSelector;
	private GuiComponentSideSelector itemSideSelector;
	private GuiComponentTab xpTab;
	private GuiComponentTab itemsTab;
	private GuiComponentLabel xpOutputsLabel;
	private GuiComponentLabel itemOutputsLabel;

	public GuiVacuumHopper(ContainerVacuumHopper container) {
		super(container, 176, 151, "openblocks.gui.vacuumhopper");

		TileEntityVacuumHopper te = container.getOwner();

		xpOutputsLabel = new GuiComponentLabel(24, 10, "XP Outputs:");
		itemOutputsLabel = new GuiComponentLabel(24, 10, "Item Outputs:");
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37, te.getTank());

		xpSideSelector = new GuiComponentSideSelector(30, 30, 40.0, te, 0, null, te.getXPOutputs(), false);
		itemSideSelector = new GuiComponentSideSelector(30, 30, 40.0, te, 0, null, te.getItemOutputs(), false);

		tabs = new GuiComponentTabs(xSize - 3, 4);

		xpTab = new GuiComponentTab(TabColor.blue.getColor(), new ItemStack(Item.expBottle, 1), 100, 100);
		xpTab.addComponent(xpSideSelector);
		xpTab.addComponent(xpOutputsLabel);

		itemsTab = new GuiComponentTab(TabColor.lightblue.getColor(), new ItemStack(Block.chest), 100, 100);
		itemsTab.addComponent(itemSideSelector);
		itemsTab.addComponent(itemOutputsLabel);

		tabs.addComponent(xpTab);
		tabs.addComponent(itemsTab);

		root.addComponent(xpLevel);
		root.addComponent(tabs);
	}
}
