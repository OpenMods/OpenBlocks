package openblocks.client.gui;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openblocks.client.gui.component.*;
import openblocks.client.gui.component.BaseComponent.TabColor;
import openblocks.common.container.ContainerXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler.AutoSlots;
import openblocks.sync.SyncableFlags;

public class GuiXPBottler extends BaseGuiContainer<ContainerXPBottler> {

	private GuiComponentTankLevel xpLevel;
	private GuiComponentProgress progress;
	private GuiComponentTabs tabs;
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

	public GuiXPBottler(ContainerXPBottler container) {
		super(container, 176, 151, "openblocks.gui.xpbottler");

		TileEntityXPBottler te = container.getTileEntity();
		int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);

		// progress bar
		progress = new GuiComponentProgress(72, 33, te.getProgress());

		// tank
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37, te.getTank());

		// create tabs
		tabs = new GuiComponentTabs(xSize - 3, 4);

		tabGlassBottle = new GuiComponentTab(TabColor.blue.getColor(), new ItemStack(Item.glassBottle, 1), 100, 100);
		tabXPBottle = new GuiComponentTab(TabColor.lightblue.getColor(), new ItemStack(Item.expBottle), 100, 100);
		tabXPFluid = new GuiComponentTab(TabColor.green.getColor(), new ItemStack(Item.bucketEmpty), 100, 100);

		// create side selectors
		sideSelectorGlassBottle = new GuiComponentSideSelector(30, 30, 40.0, null, meta, OpenBlocks.Blocks.xpBottler, te.getGlassSides(), true);
		sideSelectorXPBottle = new GuiComponentSideSelector(30, 30, 40.0, null, meta, OpenBlocks.Blocks.xpBottler, te.getXPBottleSides(), true);
		sideSelectorXPFluid = new GuiComponentSideSelector(30, 30, 40.0, null, meta, OpenBlocks.Blocks.xpBottler, te.getXPSides(), true);

		// create checkboxes
		SyncableFlags autoFlags = te.getAutomaticSlots();
		checkboxInsertGlass = new GuiComponentCheckbox(10, 82, autoFlags, AutoSlots.input.ordinal(), 0xFFFFFF);
		checkboxEjectXP = new GuiComponentCheckbox(10, 82, autoFlags, AutoSlots.output.ordinal(), 0xFFFFFF);
		checkboxDrinkXP = new GuiComponentCheckbox(10, 82, autoFlags, AutoSlots.xp.ordinal(), 0xFFFFFF);

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

		tabs.addComponent(tabGlassBottle);
		tabs.addComponent(tabXPBottle);
		tabs.addComponent(tabXPFluid);

		panel.addComponent(xpLevel);
		panel.addComponent(tabs);
		panel.addComponent(progress);
	}

}
