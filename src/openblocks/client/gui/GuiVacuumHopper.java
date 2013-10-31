package openblocks.client.gui;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openblocks.client.gui.component.*;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.tileentity.TileEntityVacuumHopper;

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

		TileEntityVacuumHopper te = container.getTileEntity();

		xpOutputsLabel = new GuiComponentLabel(24, 10, "XP Outputs:");
		itemOutputsLabel = new GuiComponentLabel(24, 10, "Item Outputs:");
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37, te.getTank());

		xpSideSelector = new GuiComponentSideSelector(30, 30, 40.0, te, 0, null, te.getXPOutputs(), false);
		itemSideSelector = new GuiComponentSideSelector(30, 30, 40.0, te, 0, null, te.getItemOutputs(), false);

		tabs = new GuiComponentTabs(xSize - 3, 4);

		xpTab = new GuiComponentTab(0xf6c3ae, new ItemStack(Item.expBottle, 1), 100, 100);
		xpTab.addComponent(xpSideSelector);
		xpTab.addComponent(xpOutputsLabel);

		itemsTab = new GuiComponentTab(0x9f95ae, new ItemStack(Block.chest), 100, 100);
		itemsTab.addComponent(itemSideSelector);
		itemsTab.addComponent(itemOutputsLabel);

		tabs.addComponent(xpTab);
		tabs.addComponent(itemsTab);

		panel.addComponent(xpLevel);
		panel.addComponent(tabs);
	}
}
