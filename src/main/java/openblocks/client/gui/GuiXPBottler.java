package openblocks.client.gui;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openblocks.common.container.ContainerXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.gui.component.BaseComponent.TabColor;

public class GuiXPBottler extends BaseGuiContainer<ContainerXPBottler> {

	private GuiComponentTankLevel xpLevel;
	private GuiComponentProgress progress;
	private GuiComponentTab tabGlassBottle;
	private GuiComponentTab tabXPBottle;
	private GuiComponentTab tabXPFluid;
	private GuiComponentSideSelector sideSelectorGlassBottle;
	private GuiComponentSideSelector sideSelectorXPBottle;
	private GuiComponentSideSelector sideSelectorXPFluid;
	private GuiComponentCheckbox checkboxInsertGlass;
	private GuiComponentCheckbox checkboxEjectXP;
	private GuiComponentCheckbox checkboxDrinkXP;
	private GuiComponentLabel labelInsertGlass;
	private GuiComponentLabel labelEjectXPBottle;
	private GuiComponentLabel labelDrinkXP;

	@Override
	protected BaseComponent createRoot() {
		TileEntityXPBottler te = getContainer().getOwner();
		int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);

		// progress bar
		progress = new GuiComponentProgress(72, 33, 0);

		// tank
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37, te.getTank());

		tabGlassBottle = new GuiComponentTab(TabColor.blue.getColor(), new ItemStack(Items.glass_bottle, 1), 100, 100);
		tabXPBottle = new GuiComponentTab(TabColor.lightblue.getColor(), new ItemStack(Items.experience_bottle), 100, 100);
		tabXPFluid = new GuiComponentTab(TabColor.green.getColor(), new ItemStack(Items.bucket), 100, 100);

		// create side selectors
		sideSelectorGlassBottle = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.xpBottler, meta, null, true);
		sideSelectorXPBottle = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.xpBottler, meta, null, true);
		sideSelectorXPFluid = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.xpBottler, meta, null, true);

		// create checkboxes
		checkboxInsertGlass = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);
		checkboxEjectXP = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);
		checkboxDrinkXP = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);

		labelInsertGlass = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoextract"));
		labelEjectXPBottle = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoeject"));
		labelDrinkXP = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autodrink"));

		tabGlassBottle.addComponent(sideSelectorGlassBottle);
		tabGlassBottle.addComponent(checkboxInsertGlass);
		tabGlassBottle.addComponent(labelInsertGlass);

		tabXPBottle.addComponent(sideSelectorXPBottle);
		tabXPBottle.addComponent(checkboxEjectXP);
		tabXPBottle.addComponent(labelEjectXPBottle);

		tabXPFluid.addComponent(sideSelectorXPFluid);
		tabXPFluid.addComponent(checkboxDrinkXP);
		tabXPFluid.addComponent(labelDrinkXP);

		BaseComponent main = super.createRoot();
		main.addComponent(xpLevel);
		main.addComponent(progress);

		GuiComponentTabWrapper tabs = new GuiComponentTabWrapper(0, 0, main);

		tabs.addComponent(tabGlassBottle);
		tabs.addComponent(tabXPBottle);
		tabs.addComponent(tabXPFluid);

		return tabs;
	}

	public GuiXPBottler(ContainerXPBottler container) {
		super(container, 176, 151, "openblocks.gui.xpbottler");
	}

}
