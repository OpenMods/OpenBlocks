package openblocks.client.gui;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.gui.component.BaseComponent.TabColor;

public class GuiVacuumHopper extends BaseGuiContainer<ContainerVacuumHopper> {
	@Override
	protected BaseComponent createRoot() {
		GuiComponentLabel xpOutputsLabel = new GuiComponentLabel(24, 10, "XP Outputs:");
		GuiComponentLabel itemOutputsLabel = new GuiComponentLabel(24, 10, "Item Outputs:");

		TileEntityVacuumHopper te = getContainer().getOwner();
		GuiComponentTankLevel xpLevel = new GuiComponentTankLevel(140, 18, 17, 37, te.getTank());

		GuiComponentSideSelector xpSideSelector = new GuiComponentSideSelector(30, 30, 40.0, null, 0, te, false);
		GuiComponentSideSelector itemSideSelector = new GuiComponentSideSelector(30, 30, 40.0, null, 0, te, false);

		GuiComponentTab xpTab = new GuiComponentTab(TabColor.blue.getColor(), new ItemStack(Items.experience_bottle, 1), 100, 100);
		xpTab.addComponent(xpSideSelector);
		xpTab.addComponent(xpOutputsLabel);

		GuiComponentTab itemsTab = new GuiComponentTab(TabColor.lightblue.getColor(), new ItemStack(Blocks.chest), 100, 100);
		itemsTab.addComponent(itemSideSelector);
		itemsTab.addComponent(itemOutputsLabel);

		BaseComponent main = super.createRoot();
		main.addComponent(xpLevel);

		GuiComponentTabWrapper tabs = new GuiComponentTabWrapper(0, 0, main);
		tabs.addComponent(xpTab);
		tabs.addComponent(itemsTab);

		return tabs;
	}

	public GuiVacuumHopper(ContainerVacuumHopper container) {
		super(container, 176, 151, "openblocks.gui.vacuumhopper");
	}
}
