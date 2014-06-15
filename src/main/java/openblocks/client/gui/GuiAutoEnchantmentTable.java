package openblocks.client.gui;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openblocks.common.container.ContainerAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.gui.component.BaseComponent.TabColor;

public class GuiAutoEnchantmentTable extends
		BaseGuiContainer<ContainerAutoEnchantmentTable> {

	// tank
	private GuiComponentTankLevel xpLevel;

	// tabs
	private GuiComponentTab tabInput;
	private GuiComponentTab tabOutput;
	private GuiComponentTab tabXP;

	// side selectors
	private GuiComponentSideSelector sideSelectorInput;
	private GuiComponentSideSelector sideSelectorOutput;
	private GuiComponentSideSelector sideSelectorXP;

	// checkboxes
	private GuiComponentCheckbox checkboxAutoExtractInput;
	private GuiComponentCheckbox checkboxAutoEjectOutput;
	private GuiComponentCheckbox checkboxAutoDrinkXP;

	// labels
	private GuiComponentLabel labelAutoExtractInput;
	private GuiComponentLabel labelAutoEjectOutput;
	private GuiComponentLabel labelAutoDrinkXP;

	private GuiComponentSlider sliderLevel;

	@Override
	protected BaseComponent createRoot() {
		TileEntityAutoEnchantmentTable te = getContainer().getOwner();
		int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);

		ItemStack enchantedAxe = new ItemStack(Items.diamond_pickaxe, 1);
		enchantedAxe.addEnchantment(Enchantment.fortune, 1);

		// create tank level
		xpLevel = new GuiComponentTankLevel(140, 30, 17, 37, te.getTank());

		// create tabs
		tabInput = new GuiComponentTab(TabColor.blue.getColor(), new ItemStack(Items.diamond_pickaxe, 1), 100, 100);
		tabOutput = new GuiComponentTab(TabColor.lightblue.getColor(), enchantedAxe, 100, 100);
		tabXP = new GuiComponentTab(TabColor.green.getColor(), new ItemStack(Items.bucket, 1), 100, 100);

		// create side selectors
		sideSelectorInput = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.autoAnvil, meta, te, true);
		sideSelectorOutput = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.autoAnvil, meta, te, true);
		sideSelectorXP = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.autoAnvil, meta, te, true);

		checkboxAutoExtractInput = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);
		checkboxAutoEjectOutput = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);
		checkboxAutoDrinkXP = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);

		// create labels
		labelAutoExtractInput = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoextract"));
		labelAutoEjectOutput = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoeject"));
		labelAutoDrinkXP = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autodrink"));

		sliderLevel = new GuiComponentSlider(44, 39, 45, 1, 30, 0);

		tabInput.addComponent(labelAutoExtractInput);
		tabInput.addComponent(checkboxAutoExtractInput);
		tabInput.addComponent(sideSelectorInput);

		tabOutput.addComponent(labelAutoEjectOutput);
		tabOutput.addComponent(checkboxAutoEjectOutput);
		tabOutput.addComponent(sideSelectorOutput);

		tabXP.addComponent(labelAutoDrinkXP);
		tabXP.addComponent(checkboxAutoDrinkXP);
		tabXP.addComponent(sideSelectorXP);

		BaseComponent main = super.createRoot();
		main.addComponent(xpLevel);
		main.addComponent(sliderLevel);

		GuiComponentTabWrapper tabs = new GuiComponentTabWrapper(0, 0, main);
		tabs.addComponent(tabInput);
		tabs.addComponent(tabOutput);
		tabs.addComponent(tabXP);

		return tabs;
	}

	public GuiAutoEnchantmentTable(ContainerAutoEnchantmentTable container) {
		super(container, 176, 175, "openblocks.gui.autoenchantmenttable");

	}

}
