package openblocks.client.gui;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openblocks.common.container.ContainerAutoAnvil;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openmods.gui.SyncedGuiContainer;
import openmods.gui.component.*;
import openmods.gui.component.BaseComponent.TabColor;

public class GuiAutoAnvil extends SyncedGuiContainer<ContainerAutoAnvil> {

	private static final ResourceLocation texture = new ResourceLocation("openmodslib:textures/gui/components.png");

	// tank
	private GuiComponentTankLevel xpLevel;

	// side selectors
	private GuiComponentSideSelector sideSelectorTool;
	private GuiComponentSideSelector sideSelectorModifier;
	private GuiComponentSideSelector sideSelectorOutput;
	private GuiComponentSideSelector sideSelectorXP;

	// tabs
	private GuiComponentTab tabTool;
	private GuiComponentTab tabModifier;
	private GuiComponentTab tabOutput;
	private GuiComponentTab tabXP;

	// checkboxes
	private GuiComponentCheckbox checkboxAutoExtractTool;
	private GuiComponentCheckbox checkboxAutoExtractModifier;
	private GuiComponentCheckbox checkboxAutoEjectOutput;
	private GuiComponentCheckbox checkboxAutoDrinkXP;

	// labels
	private GuiComponentLabel labelAutoExtractTool;
	private GuiComponentLabel labelAutoExtractModifier;
	private GuiComponentLabel labelAutoEjectOutput;
	private GuiComponentLabel labelAutoDrinkXP;

	// sprites
	private GuiComponentSprite spriteHammer;
	private GuiComponentSprite spritePlus;

	@Override
	protected BaseComponent createRoot() {
		TileEntityAutoAnvil te = getContainer().getOwner();
		int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);

		ItemStack enchantedAxe = new ItemStack(Items.diamond_pickaxe, 1);
		enchantedAxe.addEnchantment(Enchantment.fortune, 1);

		// create tank level
		xpLevel = new GuiComponentTankLevel(140, 30, 17, 37, te.getTank());

		// create tabs
		tabTool = new GuiComponentTab(TabColor.blue.getColor(), new ItemStack(Items.diamond_pickaxe, 1), 100, 100);
		tabModifier = new GuiComponentTab(TabColor.lightblue.getColor(), new ItemStack(Items.enchanted_book, 1), 100, 100);
		tabOutput = new GuiComponentTab(TabColor.green.getColor(), enchantedAxe, 100, 100);
		tabXP = new GuiComponentTab(TabColor.yellow.getColor(), new ItemStack(Items.bucket, 1), 100, 100);

		// create side selectors
		sideSelectorTool = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.autoAnvil, meta, te, true);
		addSyncUpdateListener(sideSelectorTool.createUpdater(te.getToolSides(), te.getToolSidesProxy()));

		sideSelectorModifier = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.autoAnvil, meta, te, true);
		sideSelectorOutput = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.autoAnvil, meta, te, true);
		sideSelectorXP = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.autoAnvil, meta, te, true);

		checkboxAutoExtractTool = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);
		checkboxAutoExtractModifier = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);
		checkboxAutoEjectOutput = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);
		checkboxAutoDrinkXP = new GuiComponentCheckbox(10, 82, false, 0xFFFFFF);

		// create labels
		labelAutoExtractTool = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoextract"));
		labelAutoExtractModifier = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoextract"));
		labelAutoEjectOutput = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoeject"));
		labelAutoDrinkXP = new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autodrink"));

		// create sprites
		spriteHammer = new GuiComponentSprite(80, 34, GuiComponentSprite.Sprites.hammer, texture);
		spritePlus = new GuiComponentSprite(36, 41, GuiComponentSprite.Sprites.plus, texture);

		// add checkboxes
		tabTool.addComponent(checkboxAutoExtractTool);
		tabModifier.addComponent(checkboxAutoExtractModifier);
		tabOutput.addComponent(checkboxAutoEjectOutput);
		tabXP.addComponent(checkboxAutoDrinkXP);

		// add side selectors
		tabTool.addComponent(sideSelectorTool);
		tabModifier.addComponent(sideSelectorModifier);
		tabOutput.addComponent(sideSelectorOutput);
		tabXP.addComponent(sideSelectorXP);

		// add labels
		tabTool.addComponent(labelAutoExtractTool);
		tabModifier.addComponent(labelAutoExtractModifier);
		tabOutput.addComponent(labelAutoEjectOutput);
		tabXP.addComponent(labelAutoDrinkXP);

		BaseComponent main = super.createRoot();
		main.addComponent(spriteHammer);
		main.addComponent(spritePlus);
		main.addComponent(xpLevel);

		GuiComponentTabWrapper tabs = new GuiComponentTabWrapper(0, 0, main);

		tabs.addComponent(tabTool);
		tabs.addComponent(tabModifier);
		tabs.addComponent(tabOutput);
		tabs.addComponent(tabXP);

		return tabs;
	}

	public GuiAutoAnvil(ContainerAutoAnvil container) {
		super(container, 176, 175, "openblocks.gui.autoanvil");
	}

}
